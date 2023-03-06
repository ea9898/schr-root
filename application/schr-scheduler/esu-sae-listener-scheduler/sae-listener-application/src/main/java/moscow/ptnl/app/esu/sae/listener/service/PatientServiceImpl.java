package moscow.ptnl.app.esu.sae.listener.service;

import moscow.ptnl.app.esu.sae.listener.service.patient.PatientInfoDTO;
import moscow.ptnl.app.esu.sae.listener.service.patient.PatientService;
import moscow.ptnl.app.util.service.BusinessUtil;
import ru.mos.emias.erp.patientservice.v2.ErpPatientPortTypeV2;
import ru.mos.emias.erp.patientservice.v2.Fault;
import ru.mos.emias.erp.patientservice.v2.types.Attachment;
import ru.mos.emias.erp.patientservice.v2.types.GetPatientRequest;
import ru.mos.emias.erp.patientservice.v2.types.GetPatientResponse;
import ru.mos.emias.erp.patientservice.v2.types.KeyValuePair;
import ru.mos.emias.erp.patientservice.v2.types.Options;
import ru.mos.emias.erp.patientservice.v2.types.Patient;
import ru.mos.emias.erp.patientservice.v2.types.PersonalData;
import ru.mos.emias.erp.patientservice.v2.types.Policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class PatientServiceImpl implements PatientService {

    @Autowired
    private ErpPatientPortTypeV2 erpPatientPortTypeV2;

    @Override
    public PatientInfoDTO getPatient(Long emiasId) throws Exception {
        PatientInfoDTO patientInfoDTO = new PatientInfoDTO();

        GetPatientRequest request = new GetPatientRequest();

        GetPatientRequest.Patient patientRequest = new GetPatientRequest.Patient();
        patientRequest.setEmiasId(emiasId);
        request.setPatient(patientRequest);

        request.setFilterIsActualPolicy(true);

        Options options = new Options();
        List<KeyValuePair> entry = options.getEntry();
        KeyValuePair keyValuePair = new KeyValuePair();
        keyValuePair.setKey("getAreaType");
        keyValuePair.setValue("true");
        entry.add(keyValuePair);
        request.setOptions(options);

        try {
            GetPatientResponse response = erpPatientPortTypeV2.getPatient(request);

            Patient patientResponse = response.getPatient();
            patientInfoDTO.setPatientId(patientResponse.getEmiasId());
            patientInfoDTO.setUkl(patientResponse.getUklErp());

            PersonalData personalData = patientResponse.getPersonalData();
            if (Objects.nonNull(personalData)) {
                String lastName = personalData.getLastName();
                patientInfoDTO.setLastName(lastName);
                String firstName = personalData.getFirstName();
                patientInfoDTO.setFirstName(firstName);
                String middleName = personalData.getMiddleName();
                patientInfoDTO.setPatronymic(middleName);
                patientInfoDTO.setBirthDate(BusinessUtil.convertGregorianToLocalDate(personalData.getBirthDate()));
                switch (personalData.getGender().getCode()) {
                    case "MALE":
                        patientInfoDTO.setGenderCode("male");
                        break;
                    case "FEMALE":
                        patientInfoDTO.setGenderCode("female");
                        break;
                    case "UNDEFINED":
                        patientInfoDTO.setGenderCode("undefined");
                        break;
                }
                patientInfoDTO.setFullName(BusinessUtil.convertFullNameToString(firstName, lastName, middleName));
            }

            List<PatientInfoDTO.Attachment> attachmentList = new ArrayList<>();
            for (Attachment attachment : patientResponse.getAttachments().getAttachment()) {
                if (attachment.isIsActual() && ("10".equals(attachment.getAreaType().getCode()) || "20".equals(attachment.getAreaType().getCode()))) {
                    PatientInfoDTO.Attachment patientInfoAttach = new PatientInfoDTO.Attachment();
                    patientInfoAttach.setId(attachment.getAttachId());
                    patientInfoAttach.setAreaId(attachment.getAreaId());
                    patientInfoAttach.setAreaTypeCode(Long.valueOf(attachment.getAreaType().getCode()));
                    patientInfoAttach.setAttachBeginDate(BusinessUtil.convertGregorianToLocalDate(attachment.getAttachBeginDate()));
                    patientInfoAttach.setMoId(attachment.getMainMoId());
                    patientInfoAttach.setMuId(attachment.getMuId());
                    patientInfoAttach.setAttachTypeCode(attachment.getAttachType().getCode());
                    patientInfoAttach.setAttachTypeName(attachment.getAttachType().getName());
                    String processOfAttachment = attachment.getProcessOfAttachment();
                    patientInfoAttach.setProcessOfAttachment(processOfAttachment);

                    if ("1".equals(processOfAttachment)) {
                        patientInfoAttach.setProcessOfAttachmentName("По территориальному принципу");
                    } else if ("2".equals(processOfAttachment)) {
                        patientInfoAttach.setProcessOfAttachmentName("По заявлению");
                    } else if ("3".equals(processOfAttachment)) {
                        patientInfoAttach.setProcessOfAttachmentName("По заявлению в электронном виде");
                    }

                    attachmentList.add(patientInfoAttach);
                }
            }
            patientInfoDTO.setAttachments(attachmentList);

            Patient.Policies policies = patientResponse.getPolicies();
            if (Objects.nonNull(policies)) {
                List<Policy> policy = policies.getPolicy();
                if (!policy.isEmpty()) {
                    patientInfoDTO.setPolicyNumber(policy.get(0).getPolicyNumber());
                    patientInfoDTO.setPolicyUpdateDate(BusinessUtil.convertGregorianToLocalDateTime(policy.get(0).getPolicyChangeDate()));
                    patientInfoDTO.setPolicyStatus(policy.get(0).getPolicyStatus().getCode());
                    patientInfoDTO.setPolicyOMSType(policy.get(0).getPolicyOMSType().getCode());
                }
            }
            return patientInfoDTO;
        } catch (Fault fault) {
            throw new Exception(fault.getMessage());
        }
    }
}
