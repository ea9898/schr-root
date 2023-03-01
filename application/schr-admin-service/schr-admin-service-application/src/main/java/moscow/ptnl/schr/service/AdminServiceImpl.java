package moscow.ptnl.schr.service;

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
import moscow.ptnl.app.infrastructure.repository.es.StudentPatientDataRepository;
import moscow.ptnl.app.security.annotation.EMIASSecured;
import moscow.ptnl.schr.error.ErrorReason;
import moscow.ptnl.schr.error.ServiceException;
import moscow.ptnl.schr.model.IndexElement;
import moscow.ptnl.schr.service.anthropometry.AnthropometryInfoDTO;
import moscow.ptnl.schr.service.consent.ConsentInfoDTO;
import moscow.ptnl.schr.service.dto.SyncDataByPatientIdRequest;
import moscow.ptnl.schr.service.dto.SyncDataByPatientIdResponse;
import moscow.ptnl.schr.service.patient.PatientInfoDTO;
import moscow.ptnl.schr.service.school.StudentInfoDTO;
import moscow.ptnl.schr.service.ws.AnthropometryServiceImpl;
import moscow.ptnl.schr.service.ws.ConsentInfoServiceImpl;
import moscow.ptnl.schr.service.ws.PatientServiceImpl;
import moscow.ptnl.schr.service.ws.SchoolServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service(AdminService.SERVICE_IMPL)
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = true)
public class AdminServiceImpl implements AdminService {

    @Autowired
    private PatientServiceImpl patientService;

    @Autowired
    private AnthropometryServiceImpl anthropometryService;

    @Autowired
    private ConsentInfoServiceImpl consentInfoService;

    @Autowired
    private SchoolServiceImpl schoolService;

    @Autowired
    private StudentPatientDataRepository studentPatientDataRepository;

    @EMIASSecured(faultClass = ServiceException.class)
    @Override
    public SyncDataByPatientIdResponse syncDataByPatientId(SyncDataByPatientIdRequest request) throws ServiceException {
        //1. Система выполняет проверку полномочий пользователя используя данные из системного параметра services.security.settings
        //сделано через аннотацию EMIASSecured

        List<IndexElement> indexElementList = new ArrayList<>();
        if (Objects.nonNull(request.getIndexElement())) {
            for (String element : request.getIndexElement()) {
                indexElementList.add(IndexElement.findIndexElement(element));
            }
        }
        // 2 Система для каждого переданного значения параметра $.patientId[*]
        for (Long patientId : request.getPatientId()) {
            StudentPatientData studentPatientData = new StudentPatientData();
            studentPatientData.setId(patientId.toString());
            // 2.1
            if (indexElementList.isEmpty() || indexElementList.contains(IndexElement.PATIENT_INFO) ||
                    indexElementList.contains(IndexElement.ATTACHMENTS) || indexElementList.contains(IndexElement.POLICY)) {
                // 2.2
                PatientInfoDTO patientInfoDTO;
                try {
                    patientInfoDTO = patientService.getPatient(patientId);
                } catch (Exception e) {
                    throw new ServiceException(ErrorReason.BAD_REQUEST, CustomErrorReason.SCHR_201.format(e));
                }
                // 2.3 - 2.4
                if (indexElementList.isEmpty() || indexElementList.contains(IndexElement.PATIENT_INFO)) {
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
                }
                // 2.5 - 2.6
                if (indexElementList.isEmpty() || indexElementList.contains(IndexElement.ATTACHMENTS)) {
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
                }
                // 2.7 - 2.8
                if (indexElementList.isEmpty() || indexElementList.contains(IndexElement.POLICY)) {
                    Policy policy = new Policy();
                    policy.setPolicyNumber(patientInfoDTO.getPolicyNumber());
                    policy.setPolicyUpdateDate(patientInfoDTO.getPolicyUpdateDate());
                    policy.setPolicyStatus(patientInfoDTO.getPolicyStatus());
                    policy.setPolicyOMSType(patientInfoDTO.getPolicyOMSType());
                    studentPatientData.setPolicy(policy);
                }
            }
            // 2.9 - 2.11
            if (indexElementList.isEmpty() || indexElementList.contains(IndexElement.STUD_INFO)) {
                try {
                    List<StudentInfoDTO> studentAttachmentsList = schoolService.getStudentAttachmentsList(patientId);
                    if (!studentAttachmentsList.isEmpty()) {
                        List<StudentAttachInfo> studInfoList = new ArrayList<>();
                        for (StudentInfoDTO studentInfoDTO : studentAttachmentsList) {
                            StudentAttachInfo studentAttachInfo = new StudentAttachInfo();
                            studentAttachInfo.setAttachId(studentInfoDTO.getAttachId());
                            studentAttachInfo.setOrganizationId(studentInfoDTO.getOrganizationId());
                            studentAttachInfo.setAreaId(studentInfoDTO.getAreaId());
                            studentAttachInfo.setStudChangeDate(studentInfoDTO.getStudChangeDate());
                            studInfoList.add(studentAttachInfo);
                        }
                        studentPatientData.setStudInfo(studInfoList);
                    }
                } catch (Exception e) {
                    throw new ServiceException(ErrorReason.BAD_REQUEST, CustomErrorReason.SCHR_202.format(e));
                }
            }
            // 2.12 - 2.14
            if (indexElementList.isEmpty() || indexElementList.contains(IndexElement.ANTHROPOMETRY_INFO)) {
                try {
                    List<AnthropometryInfoDTO> dtoAnthropometryInfoList = anthropometryService.getAnthropometryDataByPatient(patientId);
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
                } catch (Exception e) {
                    throw new ServiceException(ErrorReason.BAD_REQUEST, CustomErrorReason.SCHR_203.format(e));
                }
            }
            // 2.15 - 2.17
            if (indexElementList.isEmpty() || indexElementList.contains(IndexElement.CONSENTS_INFO)) {
                try {
                    List<ConsentInfoDTO> dtoConsentInfoList = consentInfoService.findConsentInfo(patientId);
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
                } catch (Exception e) {
                    throw new ServiceException(ErrorReason.BAD_REQUEST, CustomErrorReason.SCHR_204.format(e));
                }
            }
            // 2.18
            studentPatientDataRepository.save(studentPatientData);
        }
        return new SyncDataByPatientIdResponse(true);
    }
}
