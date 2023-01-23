package moscow.ptnl.app.esu.ecppd.listener.model.erp;

import java.time.LocalDate;

public class AttachmentData {

    private Long attachId;
    private Long organizationId;
    private Long areaId;
    private LocalDate attachStartDate;
    private String classIdMesh;
    private Long educationFormId;
    private String educationFormName;
    private LocalDate trainingBeginDate;
    private LocalDate trainingEndDate;
    private Long academicYearId;
    private String academicYearName;
    private Boolean isActual;

    public AttachmentData() {
    }

    public Long getAttachId() {
        return attachId;
    }

    public void setAttachId(Long attachId) {
        this.attachId = attachId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public LocalDate getAttachStartDate() {
        return attachStartDate;
    }

    public void setAttachStartDate(LocalDate attachStartDate) {
        this.attachStartDate = attachStartDate;
    }

    public String getClassIdMesh() {
        return classIdMesh;
    }

    public void setClassIdMesh(String classIdMesh) {
        this.classIdMesh = classIdMesh;
    }

    public Long getEducationFormId() {
        return educationFormId;
    }

    public void setEducationFormId(Long educationFormId) {
        this.educationFormId = educationFormId;
    }

    public String getEducationFormName() {
        return educationFormName;
    }

    public void setEducationFormName(String educationFormName) {
        this.educationFormName = educationFormName;
    }

    public LocalDate getTrainingBeginDate() {
        return trainingBeginDate;
    }

    public void setTrainingBeginDate(LocalDate trainingBeginDate) {
        this.trainingBeginDate = trainingBeginDate;
    }

    public LocalDate getTrainingEndDate() {
        return trainingEndDate;
    }

    public void setTrainingEndDate(LocalDate trainingEndDate) {
        this.trainingEndDate = trainingEndDate;
    }

    public Long getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(Long academicYearId) {
        this.academicYearId = academicYearId;
    }

    public String getAcademicYearName() {
        return academicYearName;
    }

    public void setAcademicYearName(String academicYearName) {
        this.academicYearName = academicYearName;
    }

    public Boolean getActual() {
        return isActual;
    }

    public void setActual(Boolean actual) {
        isActual = actual;
    }
}
