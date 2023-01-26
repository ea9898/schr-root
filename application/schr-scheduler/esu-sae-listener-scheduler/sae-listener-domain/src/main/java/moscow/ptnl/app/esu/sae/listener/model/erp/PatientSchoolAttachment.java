package moscow.ptnl.app.esu.sae.listener.model.erp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientSchoolAttachment {

    private Long patientId;

    private String studentId;

    private String studentPersonId;

    private LocalDateTime updateDate;

    private Boolean attachmentId;

    private Boolean organizationId;

    private Boolean areaId;

    private Boolean attachStartDate;

    private Boolean classUid;

    private Boolean educationForm;

    private Boolean educationFormName;

    private Boolean trainingBeginDate;

    private Boolean trainingEndDate;

    private Boolean academicYearId;

    private Boolean academicYear;

    private Boolean actual;

    public static PatientSchoolAttachment build(Long patientId, String studentId, String studentPersonId, LocalDateTime updateDate,
                                                Boolean attachmentId, Boolean organizationId, Boolean areaId, Boolean attachStartDate,
                                                Boolean classUid, Boolean educationForm, Boolean educationFormName,
                                                Boolean trainingBeginDate, Boolean trainingEndDate, Boolean academicYearId,
                                                Boolean academicYear, Boolean actual) {
        PatientSchoolAttachment patientSchoolAttachment = new PatientSchoolAttachment();

        patientSchoolAttachment.patientId = patientId;
        patientSchoolAttachment.studentId = studentId;
        patientSchoolAttachment.studentPersonId = studentPersonId;
        patientSchoolAttachment.updateDate = updateDate;
        patientSchoolAttachment.attachmentId = attachmentId;
        patientSchoolAttachment.organizationId = organizationId;
        patientSchoolAttachment.areaId = areaId;
        patientSchoolAttachment.attachStartDate = attachStartDate;
        patientSchoolAttachment.classUid = classUid;
        patientSchoolAttachment.educationForm = educationForm;
        patientSchoolAttachment.educationFormName = educationFormName;
        patientSchoolAttachment.trainingBeginDate = trainingBeginDate;
        patientSchoolAttachment.trainingEndDate = trainingEndDate;
        patientSchoolAttachment.academicYearId = academicYearId;
        patientSchoolAttachment.academicYear = academicYear;
        patientSchoolAttachment.actual = actual;

        return patientSchoolAttachment;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentPersonId() {
        return studentPersonId;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public Boolean getAttachmentId() {
        return attachmentId;
    }

    public Boolean getOrganizationId() {
        return organizationId;
    }

    public Boolean getAreaId() {
        return areaId;
    }

    public Boolean getAttachStartDate() {
        return attachStartDate;
    }

    public Boolean getClassUid() {
        return classUid;
    }

    public Boolean getEducationForm() {
        return educationForm;
    }

    public Boolean getEducationFormName() {
        return educationFormName;
    }

    public Boolean getTrainingBeginDate() {
        return trainingBeginDate;
    }

    public Boolean getTrainingEndDate() {
        return trainingEndDate;
    }

    public Boolean getAcademicYearId() {
        return academicYearId;
    }

    public Boolean getAcademicYear() {
        return academicYear;
    }

    public Boolean getActual() {
        return actual;
    }
}
