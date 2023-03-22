package moscow.ptnl.app.esu.ecppd.listener.model.erp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientPersonalData {

    private Long patientId;

    private String ukl;

    private String lastName;

    private String firstName;

    private String patronymic;

    private LocalDate birthDate;

    private String genderCode;

    private LocalDateTime deathDate;

    private LocalDateTime updateDate;

    public static PatientPersonalData build(Long patientId, String ukl, String lastName, String firstName, String patronymic,
                                            LocalDate birthDate, String genderCode, LocalDateTime deathDate, LocalDateTime updateDate) {
        PatientPersonalData newPersonalData = new PatientPersonalData();

        newPersonalData.patientId = patientId;
        newPersonalData.ukl = ukl;
        newPersonalData.lastName = lastName;
        newPersonalData.firstName = firstName;
        newPersonalData.patronymic = patronymic;
        newPersonalData.birthDate = birthDate;
        newPersonalData.genderCode = genderCode;
        newPersonalData.deathDate = deathDate;
        newPersonalData.updateDate = updateDate;

        return newPersonalData;
    }

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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    public LocalDateTime getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(LocalDateTime deathDate) {
        this.deathDate = deathDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}
