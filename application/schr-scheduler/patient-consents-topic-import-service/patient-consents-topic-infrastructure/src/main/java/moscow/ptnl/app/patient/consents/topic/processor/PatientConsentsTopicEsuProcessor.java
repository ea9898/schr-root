package moscow.ptnl.app.patient.consents.topic.processor;

import moscow.ptnl.app.patient.consents.topic.deserializer.PatientConsentsTopicDeserializer;
import moscow.ptnl.app.patient.consents.topic.model.ImmunoTestKind;
import moscow.ptnl.app.patient.consents.topic.model.Immunodiagnostic;
import moscow.ptnl.app.patient.consents.topic.model.PatientConsents;
import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.esu.EsuConsumerProcessor;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.patient.consents.topic.validator.PatientConsentsTopicValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * I_SCHR_9 - Получение сообщений из топика PatientConsentsTopic
 *
 * @author sevgeniy
 */
@Component
public class PatientConsentsTopicEsuProcessor extends EsuConsumerProcessor {

    @Autowired

    private PatientConsentsTopicValidator patientConsentsTopicValidator;

    @Autowired
    private PatientConsentsTopicDeserializer patientConsentsTopicDeserializer;

    @Override
    public TopicType getTopicType() {
        return TopicType.PATIENT_CONSENTS_TOPIC;
    }

    @Override
    protected Optional<String> validate(String message) {
        //4.1. Система проверяет, что сообщение соответствует формату JSON и возможно произвести его парсинг
        Optional<String> errorMsg = patientConsentsTopicValidator.validate(message);

        if (errorMsg.isEmpty()) {
            HashSet<String> errorFields = new HashSet<>();
            //4.2. Проводится форматно-логический контроль полученных данных на наличие обязательных атрибутов
            PatientConsents content;

            try {
                content = patientConsentsTopicDeserializer.apply(message);
            } catch (Exception ex) {
                return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(ex.getMessage()));
            }

            errorFields.add(content.getConsentDetails().getConsentId() == null ? "consentId" : null);
            errorFields.add(content.getConsentDetails().getDocumentedConsent().getDocumentId() == null ? "documentId" : null);
            errorFields.add(content.getConsentDetails().getDocumentedConsent().getCreateDate() == null ? "createDate" : null);
            errorFields.add(content.getConsentDetails().getDocumentedConsent().getLocationId() == null ? "locationId" : null);
            errorFields.add(content.getConsentDetails().getDocumentedConsent().getLocationName() == null ? "locationName" : null);
            errorFields.add(content.getConsentDetails().getDocumentedConsent().getAllMedicalIntervention() == null ? "allMedicalIntervention" : null);

            if (checkArrayIsNullOrHasNulls(content.getConsentDetails().getDocumentedConsent().getInterventionDetails().getMedInterventionId())) {
                errorFields.add("medInterventionId");
            }

            if (content.getConsentDetails().getDocumentedConsent().getImmunodiagnostics() == null ||
                    checkArrayIsNullOrHasNulls(content.getConsentDetails().getDocumentedConsent().getImmunodiagnostics().getImmunodiagnostic()) ||
                    content.getConsentDetails().getDocumentedConsent().getImmunodiagnostics().getImmunodiagnostic().stream().map(Immunodiagnostic::getImmunoTestKind).anyMatch(Objects::isNull) ||
                    content.getConsentDetails().getDocumentedConsent().getImmunodiagnostics().getImmunodiagnostic().stream().map(Immunodiagnostic::getImmunoTestKind).map(ImmunoTestKind::getImmunoKindCode).anyMatch(Objects::isNull)
            ) {
                errorFields.add("immunoKindCode");
            }

            if (content.getConsentDetails().getDocumentedConsent().getImmunodiagnostics() == null ||
                    checkArrayIsNullOrHasNulls(content.getConsentDetails().getDocumentedConsent().getImmunodiagnostics().getImmunodiagnostic()) ||
                    content.getConsentDetails().getDocumentedConsent().getImmunodiagnostics().getImmunodiagnostic().stream().map(Immunodiagnostic::getImmunoTestKind).anyMatch(Objects::isNull) ||
                    content.getConsentDetails().getDocumentedConsent().getImmunodiagnostics().getImmunodiagnostic().stream().map(Immunodiagnostic::getImmunoTestKind).map(ImmunoTestKind::getInfectionCode).anyMatch(Objects::isNull)
            ) {
                errorFields.add("infectionCode");
            }

            if (content.getConsentDetails().getDocumentedConsent().getImmunodiagnostics() == null ||
                    checkArrayIsNullOrHasNulls(content.getConsentDetails().getDocumentedConsent().getImmunodiagnostics().getImmunodiagnostic()) ||
                    content.getConsentDetails().getDocumentedConsent().getImmunodiagnostics().getImmunodiagnostic().stream().map(Immunodiagnostic::getImmunoDrugsTns).anyMatch(Objects::isNull) ||
                    content.getConsentDetails().getDocumentedConsent().getImmunodiagnostics().getImmunodiagnostic().stream().map(i -> i.getImmunoDrugsTns().getImmunoDrugsTnCode()).anyMatch(Objects::isNull)
            ) {
                errorFields.add("immunoDrugsTnCode");
            }

            errorFields.add(content.getConsentDetails().getDocumentedConsent().getRepresentativeDocumentId() == null ? "representativeDocumentId" : null);
            errorFields.add(content.getConsentDetails().getDocumentedConsent().getSignedByPatient() == null ? "signedByPatient" : null);
            errorFields.add(content.getConsentDetails().getDocumentedConsent().getCancelReasonId() == null ? "cancelReasonId" : null);
            errorFields.add(content.getConsentDetails().getDocumentedConsent().getCancelReasonOther() == null ? "cancelReasonOther" : null);
            errorFields.add(content.getConsentDetails().getDocumentedConsent().getMoId() == null ? "moId" : null);
            errorFields.add(content.getConsentDetails().getDocumentedConsent().getMoName() == null ? "moName" : null);
            errorFields.add(content.getConsentDetails().getIssueDateTime() == null ? "issueDateTime" : null);
            errorFields.add(content.getConsentDetails().getConsentFormId() == null ? "consentFormId" : null);
            errorFields.add(content.getConsentDetails().getConsentTypeId() == null ? "consentTypeId" : null);
            errorFields.add(content.getConsentDetails().getRepresentativePhysicalId() == null ? "representativePhysicalId" : null);
            errorFields.add(content.getConsentDetails().getRepresentativeLegalId() == null ? "representativeLegalId" : null);
            errorFields.remove(null);

            if (!errorFields.isEmpty()) {
                return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(
                        "Пустое или некорректное значение полей: " + String.join(", ", errorFields)));
            }
        }
        return errorMsg;
    }

    private boolean checkArrayIsNullOrHasNulls(List<?> array) {
        if (array == null) {
            return true;
        }
        for (Object value : array) {
            if (value == null) {
                return true;
            }
        }
        return false;
    }
}
