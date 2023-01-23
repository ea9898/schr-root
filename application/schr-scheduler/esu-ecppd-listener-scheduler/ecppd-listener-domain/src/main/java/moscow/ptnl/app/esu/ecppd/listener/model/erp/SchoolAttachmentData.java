package moscow.ptnl.app.esu.ecppd.listener.model.erp;

import java.time.LocalDateTime;
import java.util.List;

public class SchoolAttachmentData {

    private Long patientId;
    private String studentId;
    private String studentPersonIdMesh;
    private LocalDateTime studChangeDate;

    private List<AttachmentData> attachmentDataList;

    public static SchoolAttachmentData build(Long patientId, String studentId, String studentPersonIdMesh,
                                             LocalDateTime studChangeDate,
                                             List<AttachmentData> attachmentDataList) {
        SchoolAttachmentData newSchoolAttachmentData = new SchoolAttachmentData();
        newSchoolAttachmentData.patientId = patientId;
        newSchoolAttachmentData.studentId = studentId;
        newSchoolAttachmentData.studentPersonIdMesh = studentPersonIdMesh;
        newSchoolAttachmentData.studChangeDate = studChangeDate;
        newSchoolAttachmentData.attachmentDataList = attachmentDataList;
        return newSchoolAttachmentData;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentPersonIdMesh() {
        return studentPersonIdMesh;
    }

    public void setStudentPersonIdMesh(String studentPersonIdMesh) {
        this.studentPersonIdMesh = studentPersonIdMesh;
    }

    public LocalDateTime getStudChangeDate() {
        return studChangeDate;
    }

    public void setStudChangeDate(LocalDateTime studChangeDate) {
        this.studChangeDate = studChangeDate;
    }

    public List<AttachmentData> getAttachmentDataList() {
        return attachmentDataList;
    }

    public void setAttachmentDataList(List<AttachmentData> attachmentDataList) {
        this.attachmentDataList = attachmentDataList;
    }
}
