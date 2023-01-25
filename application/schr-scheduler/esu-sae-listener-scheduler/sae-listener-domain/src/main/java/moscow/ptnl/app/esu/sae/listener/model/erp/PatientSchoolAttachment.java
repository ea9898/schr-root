package moscow.ptnl.app.esu.sae.listener.model.erp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientSchoolAttachment {

    private Long patientId;

    private Long attachmentId;

    private Long organizationId;

    private Long areaId;

    private LocalDate attachStartDate;

    private String studentId;

    private String studentPersonId;

    private String classUid;

    private Long educationFormId;

    private String educationForm;

    private LocalDate trainingBeginDate;

    private LocalDate trainingEndDate;

    private Integer academicYearId;

    private String academicYear;

    private Boolean actual;

    private LocalDateTime updateDate;

    public static PatientSchoolAttachment build(Long patientId, Long attachmentId, Long organizationId, Long areaId,
                                                LocalDate attachStartDate, String studentId, String studentPersonId,
                                                String classUid, Long educationFormId, String educationForm,
                                                LocalDate trainingBeginDate, LocalDate trainingEndDate,
                                                Long academicYearId, String academicYear, Boolean actual,
                                                LocalDateTime updateDate) {
        PatientSchoolAttachment newAttachment = new PatientSchoolAttachment();

        newAttachment.patientId = patientId;
        newAttachment.attachmentId = attachmentId;
        newAttachment.organizationId = organizationId;
        newAttachment.areaId = areaId;
        newAttachment.attachStartDate = attachStartDate;
        newAttachment.studentId = studentId;
        newAttachment.studentPersonId = studentPersonId;
        newAttachment.classUid = classUid;
        newAttachment.educationFormId = educationFormId;
        newAttachment.educationForm = educationForm;
        newAttachment.trainingBeginDate = trainingBeginDate;
        newAttachment.trainingEndDate = trainingEndDate;
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

    public Long getAreaId() {
        return areaId;
    }

    public LocalDate getAttachStartDate() {
        return attachStartDate;
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

    public Long getEducationFormId() {
        return educationFormId;
    }

    public String getEducationForm() {
        return educationForm;
    }

    public LocalDate getTrainingBeginDate() {
        return trainingBeginDate;
    }

    public LocalDate getTrainingEndDate() {
        return trainingEndDate;
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
