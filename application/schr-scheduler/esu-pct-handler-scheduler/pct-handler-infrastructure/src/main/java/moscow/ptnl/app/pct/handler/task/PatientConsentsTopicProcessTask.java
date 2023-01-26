package moscow.ptnl.app.pct.handler.task;

import deserializer.PatientConsentsTopicDeserializer;

import model.PatientConsents;

import moscow.ptnl.app.domain.model.es.ConsentInfo;
import moscow.ptnl.app.domain.model.es.DocumentedConsent;
import moscow.ptnl.app.domain.model.es.ImmunoDrugsTns;
import moscow.ptnl.app.domain.model.es.ImmunoTestKind;
import moscow.ptnl.app.domain.model.es.Immunodiagnostic;
import moscow.ptnl.app.domain.model.es.Immunodiagnostics;
import moscow.ptnl.app.domain.model.es.InterventionDetails;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.infrastructure.repository.es.StudentPatientDataRepository;
import moscow.ptnl.app.model.PlannersEnum;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.task.BaseEsuProcessorTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * I_SCHR_10 - Обработка сообщений топика PatientConsentsTopic
 * 
 * @author sorlov
 */
@Component
@PropertySource("classpath:esu.properties")
public class PatientConsentsTopicProcessTask extends BaseEsuProcessorTask {

    @Autowired
    private PatientConsentsTopicDeserializer patientConsentsTopicDeserializer;

    @Autowired
    private StudentPatientDataRepository studentPatientDataRepository;

    @Override
    protected Optional<String> processMessage(String inputMsg) {
        //4.2 Система выполняет парсинг сообщения
        PatientConsents content;

        try {
            content = patientConsentsTopicDeserializer.apply(inputMsg);
        } catch (Exception ex) {
            return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(ex.getMessage()));
        }
        return transactions.execute(s -> {
            //4.3 Поиск документа в индексе student_patient_registry
            Optional<StudentPatientData> studentPatientData = studentPatientDataRepository.findById(content.getPatientId().toString());

            if (studentPatientData.isEmpty()
                    || studentPatientData.get().getPatientInfo() == null
                    || !Objects.equals(studentPatientData.get().getId(), String.valueOf(studentPatientData.get().getPatientInfo().getPatientId()))) {
                return Optional.of(CustomErrorReason.PATIENT_NOT_FOUND.format(content.getPatientId()));
            }
            //4.4 Для документа найденного на шаге 4.3, выполняется поиск блоков в элементе индекса consentsInfo
            Iterator<ConsentInfo> consentsInfoIterator = studentPatientData.get().getConsentsInfo().listIterator();

            while (consentsInfoIterator.hasNext()) {
                ConsentInfo consentInfo = consentsInfoIterator.next();

                if (Objects.equals(consentInfo.getConsentId(), content.getConsentDetails().getConsentId())) {
                    if (!content.getConsentDetails().getIssueDateTime().isAfter(consentInfo.getIssueDateTime())) {
                        return Optional.of(CustomErrorReason.INFORMATION_IS_OUTDATED.format());
                    }
                    consentsInfoIterator.remove();
                }
            }
            //4.5 Система записывает блок в элемент индекса consentsInfo документа найденного на шаге
            applyData(content, studentPatientData.get());

            if (studentPatientData.get().getConsentsInfo().isEmpty()) {
                studentPatientData.get().setConsentsInfo(null);
            }
            studentPatientDataRepository.save(studentPatientData.get());

            return Optional.empty();
        });
    }

    private void applyData(PatientConsents newData, StudentPatientData entity) {
        ConsentInfo consentInfo = new ConsentInfo();
        consentInfo.setConsentId(newData.getConsentDetails().getConsentId());
        consentInfo.setIssueDateTime(newData.getConsentDetails().getIssueDateTime());
        consentInfo.setConsentFormId(newData.getConsentDetails().getConsentFormId());
        consentInfo.setConsentTypeId(newData.getConsentDetails().getConsentTypeId());
        consentInfo.setRepresentativeLegalId(newData.getConsentDetails().getRepresentativeLegalId());
        consentInfo.setRepresentativePhysicalId(newData.getConsentDetails().getRepresentativePhysicalId());

        if (newData.getConsentDetails().getDocumentedConsent() != null) {
            consentInfo.setDocumentedConsent(new DocumentedConsent());
            applyData(newData.getConsentDetails().getDocumentedConsent(), consentInfo.getDocumentedConsent());
        }
        if (entity.getConsentsInfo() == null) {
            entity.setConsentsInfo(new ArrayList<>());
        }
        entity.getConsentsInfo().add(consentInfo);
    }

    private void applyData(model.DocumentedConsent newData, DocumentedConsent documentedConsent) {
        documentedConsent.setCreateDate(newData.getCreateDate());
        documentedConsent.setLocationId(newData.getLocationId());
        documentedConsent.setLocationName(newData.getLocationName());
        documentedConsent.setAllMedicalIntervention(newData.getAllMedicalIntervention());
        documentedConsent.setRepresentativeDocumentId(newData.getRepresentativeDocumentId());
        documentedConsent.setSignedByPatient(newData.getSignedByPatient());
        documentedConsent.setCancelReasonId(newData.getCancelReasonId());
        documentedConsent.setCancelReasonOther(newData.getCancelReasonOther());
        documentedConsent.setMoId(newData.getMoId());
        documentedConsent.setMoName(newData.getMoName());

        if (newData.getInterventionDetails() != null
                && newData.getInterventionDetails().getMedInterventionId() != null
                && !newData.getInterventionDetails().getMedInterventionId().isEmpty()) {
            documentedConsent.setInterventionDetails(new InterventionDetails());
            documentedConsent.getInterventionDetails().setMedInterventionId(newData.getInterventionDetails().getMedInterventionId());
        }
        if (newData.getImmunodiagnostics() != null
                && newData.getImmunodiagnostics().getImmunodiagnostic() != null
                && !newData.getImmunodiagnostics().getImmunodiagnostic().isEmpty()) {
            documentedConsent.setImmunodiagnostics(new Immunodiagnostics());
            documentedConsent.getImmunodiagnostics().setImmunodiagnostic(newData.getImmunodiagnostics().getImmunodiagnostic().stream()
                    .map(i -> {
                        Immunodiagnostic immunodiagnostic = new Immunodiagnostic();

                        if (i.getImmunoDrugsTns() != null) {
                            immunodiagnostic.setImmunoDrugsTns(new ImmunoDrugsTns());
                            immunodiagnostic.getImmunoDrugsTns().setImmunoDrugsTnCode(i.getImmunoDrugsTns().getImmunoDrugsTnCode());
                        }
                        if (i.getImmunoTestKind() != null) {
                            immunodiagnostic.setImmunoTestKind(new ImmunoTestKind());
                            immunodiagnostic.getImmunoTestKind().setImmunoKindCode(i.getImmunoTestKind().getImmunoKindCode());
                            immunodiagnostic.getImmunoTestKind().setInfectionCode(i.getImmunoTestKind().getInfectionCode());
                        }
                        return immunodiagnostic;
                    })
                    .collect(Collectors.toList())
            );
        }
    }

    @Override
    protected final PlannersEnum getPlanner() {
        return PlannersEnum.I_SCHR_10;
    }

    @Override
    protected TopicType getTopic() {
        return TopicType.PATIENT_CONSENTS_TOPIC;
    }
}
