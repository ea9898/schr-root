package moscow.ptnl.schr.service.ws;

import moscow.ptnl.schr.service.school.SchoolService;
import moscow.ptnl.schr.service.school.StudentInfoDTO;
import net.ptnl.erp.school.v1.school_service.SchoolFault;
import net.ptnl.erp.school.v1.school_service.model.GetStudentAttachmentsListRequest;
import net.ptnl.erp.school.v1.school_service.model.GetStudentAttachmentsListResponse;
import net.ptnl.erp.school.v1.school_service.model.OutputAttachment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class SchoolServiceImpl implements SchoolService {

    @Autowired
    private net.ptnl.erp.school.v1.school_service.SchoolService schoolService;

    @Override
    public List<StudentInfoDTO> getStudentAttachmentsList(Long emiasId) throws Exception {
        GetStudentAttachmentsListRequest request = new GetStudentAttachmentsListRequest();
        GetStudentAttachmentsListRequest.Patients valueRequest = new GetStudentAttachmentsListRequest.Patients();
        List<GetStudentAttachmentsListRequest.Patients.Patient> patientList = valueRequest.getPatient();
        GetStudentAttachmentsListRequest.Patients.Patient patientRequest = new GetStudentAttachmentsListRequest.Patients.Patient();
        patientRequest.setEmiasId(emiasId);
        patientList.add(patientRequest);
        request.setPatients(valueRequest);
        try {
            List<StudentInfoDTO> studentInfoDTOList = new ArrayList<>();
            GetStudentAttachmentsListResponse response = schoolService.getStudentAttachmentsList(request);
            for (GetStudentAttachmentsListResponse.ResultRequests.ResultRequest result : response.getResultRequests().getResultRequest()) {
                for (OutputAttachment attachment : result.getPatient().getActualAttachments().getActualAttachment()) {
                    StudentInfoDTO studentInfoDTO = new StudentInfoDTO();
                    studentInfoDTO.setAttachId(attachment.getAttachId());
                    studentInfoDTO.setOrganizationId(attachment.getMoId());
                    studentInfoDTO.setAreaId(attachment.getAreaId());
                    studentInfoDTO.setStudChangeDate(LocalDateTime.now());
                    studentInfoDTOList.add(studentInfoDTO);
                }
            }
            return studentInfoDTOList;
        } catch (SchoolFault fault) {
            throw new Exception(fault.getMessage());
        }
    }

}
