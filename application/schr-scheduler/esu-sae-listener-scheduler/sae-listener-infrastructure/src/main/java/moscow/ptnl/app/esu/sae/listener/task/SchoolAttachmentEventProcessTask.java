package moscow.ptnl.app.esu.sae.listener.task;

import moscow.ptnl.app.domain.model.es.AnthropometryInfo;
import moscow.ptnl.app.domain.model.es.Attachment;
import moscow.ptnl.app.domain.model.es.ConsentInfo;
import moscow.ptnl.app.domain.model.es.DocumentedConsent;
import moscow.ptnl.app.domain.model.es.ImmunoDrugsTns;
import moscow.ptnl.app.domain.model.es.ImmunoTestKind;
import moscow.ptnl.app.domain.model.es.Immunodiagnostic;
import moscow.ptnl.app.domain.model.es.Immunodiagnostics;
import moscow.ptnl.app.domain.model.es.InterventionDetails;
import moscow.ptnl.app.domain.model.es.PatientInfo;
import moscow.ptnl.app.domain.model.es.Policy;
import moscow.ptnl.app.domain.model.es.StudentAttachInfo;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.esu.sae.listener.deserializer.PatientSchoolAttachmentDeserializer;
import moscow.ptnl.app.esu.sae.listener.model.erp.PatientSchoolAttachment;
import moscow.ptnl.app.esu.sae.listener.service.anthropometry.AnthropometryInfoDTO;
import moscow.ptnl.app.esu.sae.listener.service.anthropometry.AnthropometryService;
import moscow.ptnl.app.esu.sae.listener.service.consent.ConsentInfoDTO;
import moscow.ptnl.app.esu.sae.listener.service.consent.ConsentInfoService;
import moscow.ptnl.app.esu.sae.listener.service.patient.PatientInfoDTO;
import moscow.ptnl.app.esu.sae.listener.service.patient.PatientService;
import moscow.ptnl.app.infrastructure.repository.es.StudentPatientDataRepository;
import moscow.ptnl.app.model.PlannersEnum;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.task.BaseEsuProcessorTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * I_SCHR_12 - Обработка сообщений топика SchoolAttachmentEvent
 */

@Component
@PropertySource("classpath:esu.properties")
public class SchoolAttachmentEventProcessTask extends BaseEsuProcessorTask {

    @Autowired
    private PatientService patientService;

    @Autowired
    private AnthropometryService anthropometryService;

    @Autowired
    private ConsentInfoService consentInfoService;


    @Autowired
    private PatientSchoolAttachmentDeserializer patientSchoolAttachmentDeserializer;

    @Autowired
    private StudentPatientDataRepository studentPatientDataRepository;

    @Override
    protected Optional<String> processMessage(String inputMsg) {
        // 4.2 Система выполняет парсинг сообщения
        List<PatientSchoolAttachment> content;
        try {
            content = patientSchoolAttachmentDeserializer.getPatientSchoolAttachments(inputMsg);
        } catch (Exception ex) {
            return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(ex.getMessage()));
        }

        return transactions.execute(s -> {
            Long patientId = content.get(0).getPatientId();
            // 4.3 Поиск документа в индексе student_patient_registry
            Optional<StudentPatientData> studentPatientDataOptional = studentPatientDataRepository.findById(patientId.toString());

            StudentPatientData studentPatientData;
            if (studentPatientDataOptional.isEmpty()) {
                studentPatientData = new StudentPatientData();
                studentPatientData.setId(patientId.toString());
                try {
                    // A_SCHR_1 пункты 1,2
                    PatientInfoDTO patientInfoDTO = patientService.getPatient(patientId);
                    // A_SCHR_1 пункты 3,4
                    List<AnthropometryInfoDTO> dtoAnthropometryInfoList = anthropometryService.getAnthropometryDataByPatient(patientId);
                    // A_SCHR_1 пункты 5,6
                    List<ConsentInfoDTO> dtoConsentInfoList = consentInfoService.findConsentInfo(patientId);

                    // A_SCHR_1 пункт 7 (patientInfo)
                    PatientInfo patientInfo = new PatientInfo();
                    patientInfo.setPatientId(patientInfoDTO.getPatientId());
                    patientInfo.setUkl(patientInfoDTO.getUkl());
                    patientInfo.setLastName(patientInfoDTO.getLastName());
                    patientInfo.setFirstName(patientInfoDTO.getFirstName());
                    patientInfo.setPatronymic(patientInfoDTO.getPatronymic());
                    patientInfo.setFullName(patientInfoDTO.getFullName());
                    patientInfo.setBirthDate(patientInfoDTO.getBirthDate());
                    patientInfo.setGenderCode(patientInfoDTO.getGenderCode());
                    studentPatientData.setPatientInfo(patientInfo);

                    // A_SCHR_1 пункт 7 (attachments)
                    List<PatientInfoDTO.Attachment> dtoAttachmentList = patientInfoDTO.getAttachments();
                    if (!dtoAttachmentList.isEmpty()) {
                        List<Attachment> entityAttachmentList = new ArrayList<>();
                        for (PatientInfoDTO.Attachment dtoAttachment : dtoAttachmentList) {
                            Attachment attachment = new Attachment();
                            attachment.setId(dtoAttachment.getId());
                            attachment.setAreaId(dtoAttachment.getAreaId());
                            attachment.setAreaTypeCode(dtoAttachment.getAreaTypeCode());
                            attachment.setAttachBeginDate(dtoAttachment.getAttachBeginDate());
                            attachment.setMoId(dtoAttachment.getMoId());
                            attachment.setMuId(dtoAttachment.getMuId());
                            attachment.setAttachTypeCode(dtoAttachment.getAttachTypeCode());
                            attachment.setAttachTypeName(dtoAttachment.getAttachTypeName());
                            attachment.setProcessOfAttachmentCode(dtoAttachment.getProcessOfAttachment());
                            attachment.setProcessOfAttachmentName(dtoAttachment.getProcessOfAttachmentName());
                            entityAttachmentList.add(attachment);
                        }
                        studentPatientData.setAttachments(entityAttachmentList);
                    }

                    // A_SCHR_1 пункт 7 (policy)
                    Policy policy = new Policy();
                    policy.setPolicyNumber(patientInfoDTO.getPolicyNumber());
                    policy.setPolicyUpdateDate(patientInfoDTO.getPolicyUpdateDate());
                    policy.setPolicyStatus(patientInfoDTO.getPolicyStatus());
                    policy.setPolicyOMSType(patientInfoDTO.getPolicyOMSType());
                    studentPatientData.setPolicy(policy);

                    // A_SCHR_1 пункт 7 (anthropometryInfo)
                    if (!dtoAnthropometryInfoList.isEmpty()) {
                        List<AnthropometryInfo> entityAnthropometryInfoList = new ArrayList<>();
                        for (AnthropometryInfoDTO dtoAnthropometryInfo : dtoAnthropometryInfoList) {
                            AnthropometryInfo anthropometryInfo = new AnthropometryInfo();
                            anthropometryInfo.setMeasurementDate(dtoAnthropometryInfo.getMeasurementDate());
                            anthropometryInfo.setDocumentId(dtoAnthropometryInfo.getDocumentId());
                            anthropometryInfo.setMeasurementType(dtoAnthropometryInfo.getMeasurementType());
                            anthropometryInfo.setMeasurementValue(dtoAnthropometryInfo.getMeasurementValue());
                            anthropometryInfo.setCentility(dtoAnthropometryInfo.getCentility());
                            anthropometryInfo.setResultAssessmentId(dtoAnthropometryInfo.getResultAssessmentId());
                            entityAnthropometryInfoList.add(anthropometryInfo);
                        }
                        studentPatientData.setAnthropometryInfo(entityAnthropometryInfoList);
                    }

                    // A_SCHR_1 пункт 7 (сonsentsInfo)
                    if (!dtoConsentInfoList.isEmpty()) {
                        List<ConsentInfo> entityConsentInfoList = new ArrayList<>();
                        for (ConsentInfoDTO dtoConsentInfo : dtoConsentInfoList) {
                            ConsentInfo consentInfo = new ConsentInfo();
                            consentInfo.setConsentId(dtoConsentInfo.getConsentId());

                            ConsentInfoDTO.DocumentedConsent dtoDocumentedConsent = dtoConsentInfo.getDocumentedConsent();
                            if (Objects.nonNull(dtoDocumentedConsent)) {
                                DocumentedConsent documentedConsent = new DocumentedConsent();
                                documentedConsent.setDocumentId(dtoDocumentedConsent.getDocumentId());
                                documentedConsent.setCreateDate(dtoDocumentedConsent.getCreateDate());
                                documentedConsent.setLocationId(dtoDocumentedConsent.getLocationId());
                                documentedConsent.setLocationName(dtoDocumentedConsent.getLocationName());
                                documentedConsent.setAllMedicalIntervention(dtoDocumentedConsent.isAllMedicalIntervention());

                                InterventionDetails interventionDetails = new InterventionDetails();
                                interventionDetails.setMedInterventionId(dtoDocumentedConsent.getInterventionDetails());
                                documentedConsent.setInterventionDetails(interventionDetails);

                                if (!dtoDocumentedConsent.getImmunodiagnostics().isEmpty()) {
                                    Immunodiagnostics immunodiagnostics = new Immunodiagnostics();
                                    List<Immunodiagnostic> immunodiagnosticList = new ArrayList<>();
                                    for (ConsentInfoDTO.DocumentedConsent.Immunodiagnostics dtoImmunodiagnostics : dtoDocumentedConsent.getImmunodiagnostics()) {
                                        Immunodiagnostic immunodiagnostic = new Immunodiagnostic();

                                        ImmunoTestKind immunoTestKind = new ImmunoTestKind();
                                        immunoTestKind.setImmunoKindCode(dtoImmunodiagnostics.getImmunoKindCode());
                                        immunoTestKind.setInfectionCode(dtoImmunodiagnostics.getInfectionCode());
                                        immunodiagnostic.setImmunoTestKind(immunoTestKind);

                                        ImmunoDrugsTns immunoDrugsTns = new ImmunoDrugsTns();
                                        immunoDrugsTns.setImmunoDrugsTnCode(dtoImmunodiagnostics.getImmunoDrugsTnCodeList());
                                        immunodiagnostic.setImmunoDrugsTns(immunoDrugsTns);

                                        immunodiagnosticList.add(immunodiagnostic);
                                    }
                                    immunodiagnostics.setImmunodiagnostic(immunodiagnosticList);
                                    documentedConsent.setImmunodiagnostics(immunodiagnostics);
                                }

                                documentedConsent.setRepresentativeDocumentId(dtoDocumentedConsent.getRepresentativeDocumentId());
                                documentedConsent.setSignedByPatient(dtoDocumentedConsent.getSignedByPatient());
                                documentedConsent.setCancelReasonId(dtoDocumentedConsent.getCancelReasonId());
                                documentedConsent.setCancelReasonOther(dtoDocumentedConsent.getCancelReasonOther());
                                documentedConsent.setMoId(dtoDocumentedConsent.getMoId());
                                documentedConsent.setMoName(dtoDocumentedConsent.getMoName());
                                consentInfo.setDocumentedConsent(documentedConsent);
                            }

                            consentInfo.setIssueDateTime(dtoConsentInfo.getIssueDate());
                            consentInfo.setConsentFormId(dtoConsentInfo.getConsentFormId());
                            consentInfo.setConsentTypeId(dtoConsentInfo.getConsentTypeId());
                            consentInfo.setRepresentativePhysicalId(dtoConsentInfo.getRepresentativePhysicalId());
                            consentInfo.setRepresentativeLegalId(dtoConsentInfo.getRepresentativeLegalId());

                            entityConsentInfoList.add(consentInfo);
                        }
                        studentPatientData.setConsentInfos(entityConsentInfoList);
                    }
                    StudentPatientData save = studentPatientDataRepository.save(studentPatientData);
                } catch (Exception e) {
                    return Optional.of(CustomErrorReason.CREATE_NEW_PATIENT_EXCEPTION.format(e.getMessage()));
                }
            } else {
                studentPatientData = studentPatientDataOptional.get();
            }
            // 4.4 Система для каждого значения из массива $.entityData[*]
            for (PatientSchoolAttachment attachment : content) {
                // блок/блоки - имеется в виду, что должен быть 1 блок, но если их больше одного, то это всё дубли
                List<StudentAttachInfo> studInfoList = new ArrayList<>();
                if (Objects.nonNull(studentPatientData.getStudInfo())) {
                    studInfoList = studentPatientData.getStudInfo().stream()
                            .filter(info -> info.getAttachId().equals(attachment.getAttachmentId()))
                            .collect(Collectors.toList());
                }
                Long ageMax = settingService.getSettingProperty(getPlanner().getPlannerName() + ".ageMax", Long.class, false);
                if (!studInfoList.isEmpty()) {
                    if (studInfoList.get(0).getStudChangeDate().isBefore(attachment.getUpdateDate())) {
                        if (attachment.getActual()) {
                            // Система удаляет найденный блок (блоки) и переходит на следующий шаг
                            studentPatientData.getStudInfo().removeAll(studInfoList);
                            applyData(attachment, studentPatientData); //4.4.2 Система записывает блок в элемент индекса studInfo
                        } else {
                            //Система удаляет найденный блок/блоки
                            studentPatientData.getStudInfo().removeAll(studInfoList);
                            studentPatientDataRepository.save(studentPatientData);
                            //ЕСЛИ после удаления блока/блоков в элементе индекса studInfo не останется блоков в массиве
                            if (studentPatientData.getStudInfo().isEmpty()) {
                                // Система проверяет возраст пациента
                                if ((LocalDate.now().getYear() - studentPatientData.getPatientInfo().getBirthDate().getYear()) >= ageMax) {
                                    // Система удаляет документ с пациентом _id = $.emiasId и переходит на шаг 4.5
                                    studentPatientDataRepository.delete(studentPatientData);
                                    return Optional.empty();
                                } else {
                                    // Система удаляет элемент индекса studInfo и переходит на шаг 4.5
                                    studentPatientData.setStudInfo(null);
                                    studentPatientDataRepository.save(studentPatientData);
                                    return Optional.empty();
                                }
                            } else {
                                // переходит на шаг 4.5
                                return Optional.empty();
                            }
                        }
                    } else {
                        // Переход на шаг 4.5 с ошибкой SCHR_107
                        return Optional.of(CustomErrorReason.INFORMATION_IS_OUTDATED.format());
                    }
                } else {
                    if (attachment.getActual()) {
                        // Система переходит на следующий шаг
                        applyData(attachment, studentPatientData); //4.4.2 Система записывает блок в элемент индекса studInfo
                    } else {
                        // Система проверяет возраст пациента
                        if ((LocalDate.now().getYear() - studentPatientData.getPatientInfo().getBirthDate().getYear()) >= ageMax) {
                            // Система удаляет документ с пациентом _id = $.emiasId и переходит на шаг 4.5 с ошибкой SCHR_106
                            studentPatientDataRepository.delete(studentPatientData);
                            return Optional.of(CustomErrorReason.REMOVING_RECORD_NOT_FOUND.format());
                        } else {
                            // Система переходит на шаг 4.5 с ошибкой SCHR_106
                            return Optional.of(CustomErrorReason.REMOVING_RECORD_NOT_FOUND.format());
                        }
                    }
                }
            }
            return Optional.empty();
        });
    }

    private void applyData(PatientSchoolAttachment newPatientSchoolAttachment, StudentPatientData entity) {
        StudentAttachInfo studentAttachInfo = new StudentAttachInfo();
        studentAttachInfo.setAttachId(newPatientSchoolAttachment.getAttachmentId());
        studentAttachInfo.setOrganizationId(newPatientSchoolAttachment.getOrganizationId());
        studentAttachInfo.setAreaId(newPatientSchoolAttachment.getAreaId());
        studentAttachInfo.setAttachStartDate(newPatientSchoolAttachment.getAttachStartDate());
        studentAttachInfo.setStudentId(newPatientSchoolAttachment.getStudentId());
        studentAttachInfo.setStudentPersonIdMesh(newPatientSchoolAttachment.getStudentPersonId());
        studentAttachInfo.setClassIdMesh(newPatientSchoolAttachment.getClassUid());
        studentAttachInfo.setEducationFormId(newPatientSchoolAttachment.getEducationFormId());
        studentAttachInfo.setEducationFormName(newPatientSchoolAttachment.getEducationForm());
        studentAttachInfo.setTrainingBeginDate(newPatientSchoolAttachment.getTrainingBeginDate());
        studentAttachInfo.setTrainingEndDate(newPatientSchoolAttachment.getTrainingEndDate());
        studentAttachInfo.setAcademicYearId(Long.valueOf(newPatientSchoolAttachment.getAcademicYearId()));
        studentAttachInfo.setAcademicYearName(newPatientSchoolAttachment.getAcademicYear());
        studentAttachInfo.setStudChangeDate(newPatientSchoolAttachment.getUpdateDate());
        if (Objects.isNull(entity.getStudInfo())) {
            entity.setStudInfo(new ArrayList<>());
        }
        entity.getStudInfo().add(studentAttachInfo);
        studentPatientDataRepository.save(entity);
    }

    @Override
    protected PlannersEnum getPlanner() {
        return PlannersEnum.I_SCHR_12;
    }

    @Override
    protected TopicType getTopic() {
        return TopicType.SCHOOL_ATTACHMENT_EVENT;
    }

}
