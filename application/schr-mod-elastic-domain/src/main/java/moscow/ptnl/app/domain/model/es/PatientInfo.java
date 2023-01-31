package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientInfo {

    @Field(type = FieldType.Long, name = "patientId")
    private Long patientId;

    @Field(type = FieldType.Keyword, name = "ukl")
    private String ukl;

    @Field(type = FieldType.Keyword, name = "lastName")
    private String lastName;

    @Field(type = FieldType.Keyword, name = "firstName")
    private String firstName;

    @Field(type = FieldType.Keyword, name = "patronymic")
    private String patronymic;

    @Field(type = FieldType.Text, name = "fullName")
    private String fullName;

    @Field(type = FieldType.Date, format = DateFormat.date, name = "birthDate")
    private LocalDate birthDate;

    @Field(type = FieldType.Keyword, name = "genderCode")
    private Long genderCode;

    @Field(type = FieldType.Date, format = DateFormat.date, name = "deathDate")
    private LocalDate deathDate;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis, name = "updateDate")
    private LocalDateTime updateDate;

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getUkl() {
        return ukl;
    }

    public void setUkl(String ukl) {
        this.ukl = ukl;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Long getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(Long genderCode) {
        this.genderCode = genderCode;
    }

    public LocalDate getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(LocalDate deathDate) {
        this.deathDate = deathDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}
