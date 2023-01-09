package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

public class StudentAttachInfo {

    @Field(type = FieldType.Long, name = "attachId")
    private Long attachId;

    @Field(type = FieldType.Long, name = "organizationId")
    private Long organizationId;

    @Field(type = FieldType.Keyword, name = "studentIdKis")
    private String studentIdKis;

    @Field(type = FieldType.Keyword, name = "studentIdMesh")
    private String studentIdMesh;

    @Field(type = FieldType.Keyword, name = "classIdMesh")
    private String classIdMesh;

    @Field(type = FieldType.Long, name = "academicYearId")
    private Long academicYearId;

    @Field(type = FieldType.Keyword, name = "academicYearName")
    private String academicYearName;

    @Field(type = FieldType.Date, name = "studChangeDate")
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

    public String getStudentIdKis() {
        return studentIdKis;
    }

    public void setStudentIdKis(String studentIdKis) {
        this.studentIdKis = studentIdKis;
    }

    public String getStudentIdMesh() {
        return studentIdMesh;
    }

    public void setStudentIdMesh(String studentIdMesh) {
        this.studentIdMesh = studentIdMesh;
    }

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
}
