package moscow.ptnl.app.esu.sae.listener.model.erp;

import java.time.LocalDateTime;

public class PatientSchoolAttachment {

    private Long patientId;

    private Long attachmentId;

    private Long organizationId;

    private String studentId;

    private String studentPersonId;

    private String classUid;

    private Integer academicYearId;

    private String academicYear;

    private Boolean actual;

    private LocalDateTime updateDate;

    public static PatientSchoolAttachment build(Long patientId, Long attachmentId, Long organizationId, String studentId, String studentPersonId,
                                   String classUid, Long academicYearId, String academicYear, Boolean actual, LocalDateTime updateDate) {
        PatientSchoolAttachment newAttachment = new PatientSchoolAttachment();

        newAttachment.patientId = patientId;
        newAttachment.attachmentId = attachmentId;
        newAttachment.organizationId = organizationId;
        newAttachment.studentId = studentId;
        newAttachment.studentPersonId = studentPersonId;
        newAttachment.classUid = classUid;
        newAttachment.academicYearId = academicYearId == null ? null : academicYearId.intValue();
        newAttachment.academicYear = academicYear;
        newAttachment.actual = actual;
        newAttachment.updateDate = updateDate;

        return newAttachment;
    }

    public Long getPatientId() {
        return patientId;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentPersonId() {
        return studentPersonId;
    }

    public String getClassUid() {
        return classUid;
    }

    public Integer getAcademicYearId() {
        return academicYearId;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public Boolean getActual() {
        return actual;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }
}
