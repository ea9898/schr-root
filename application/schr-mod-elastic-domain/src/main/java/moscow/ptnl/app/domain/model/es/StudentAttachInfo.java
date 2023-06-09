package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentAttachInfo {

    @Field(type = FieldType.Long, name = "attachId")
    private Long attachId;

    @Field(type = FieldType.Long, name = "organizationId")
    private Long organizationId;

    @Field(type = FieldType.Long, name = "areaId")
    private Long areaId;

    @Field(type = FieldType.Date, format = DateFormat.date, name = "attachStartDate")
    private LocalDate attachStartDate;

    @Field(type = FieldType.Keyword, name = "studentId")
    private String studentId;

    @Field(type = FieldType.Keyword, name = "studentPersonIdMesh")
    private String studentPersonIdMesh;

    @Field(type = FieldType.Keyword, name = "classIdMesh")
    private String classIdMesh;

    @Field(type = FieldType.Long, name = "educationFormId")
    private Long educationFormId;

    @Field(type = FieldType.Keyword, name = "educationFormName")
    private String educationFormName;

    @Field(type = FieldType.Date, format = DateFormat.date, name = "trainingBeginDate")
    private LocalDate trainingBeginDate;

    @Field(type = FieldType.Date, format = DateFormat.date, name = "trainingEndDate")
    private LocalDate trainingEndDate;

    @Field(type = FieldType.Long, name = "academicYearId")
    private Long academicYearId;

    @Field(type = FieldType.Keyword, name = "academicYearName")
    private String academicYearName;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis, name = "studChangeDate")
    private LocalDateTime studChangeDate;

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

    public String getStudentId() { return studentId; }

    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentPersonIdMesh() { return studentPersonIdMesh; }

    public void setStudentPersonIdMesh(String studentPersonIdMesh) { this.studentPersonIdMesh = studentPersonIdMesh; }

    public String getClassIdMesh() {
        return classIdMesh;
    }

    public void setClassIdMesh(String classIdMesh) {
        this.classIdMesh = classIdMesh;
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

    public LocalDateTime getStudChangeDate() {
        return studChangeDate;
    }

    public void setStudChangeDate(LocalDateTime studChangeDate) {
        this.studChangeDate = studChangeDate;
    }

    public Long getAreaId() { return areaId; }

    public void setAreaId(Long areaId) { this.areaId = areaId; }

    public LocalDate getAttachStartDate() { return attachStartDate; }

    public void setAttachStartDate(LocalDate attachStartDate) { this.attachStartDate = attachStartDate; }

    public Long getEducationFormId() { return educationFormId; }

    public void setEducationFormId(Long educationFormId) { this.educationFormId = educationFormId; }

    public String getEducationFormName() { return educationFormName; }

    public void setEducationFormName(String educationFormName) { this.educationFormName = educationFormName; }

    public LocalDate getTrainingBeginDate() { return trainingBeginDate; }

    public void setTrainingBeginDate(LocalDate trainingBeginDate) { this.trainingBeginDate = trainingBeginDate; }

    public LocalDate getTrainingEndDate() { return trainingEndDate; }

    public void setTrainingEndDate(LocalDate trainingEndDate) { this.trainingEndDate = trainingEndDate; }
}
