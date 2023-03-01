package moscow.ptnl.schr.service.school;

import java.util.List;

public interface SchoolService {

    List<StudentInfoDTO> getStudentAttachmentsList(Long emiasId) throws Exception;

}
