package moscow.ptnl.app.esu.sae.listener.service.patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PatientInfoDTO {

    private Long patientId;
    private String ukl;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String fullName;
    private LocalDate birthDate;
    private String genderCode;

    private List<Attachment> attachments;

    private String policyNumber;
    private LocalDateTime policyUpdateDate;
    private String policyStatus;
    private String policyOMSType;

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

    public String getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public LocalDateTime getPolicyUpdateDate() {
        return policyUpdateDate;
    }

    public void setPolicyUpdateDate(LocalDateTime policyUpdateDate) {
        this.policyUpdateDate = policyUpdateDate;
    }

    public String getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
    }

    public String getPolicyOMSType() {
        return policyOMSType;
    }

    public void setPolicyOMSType(String policyOMSType) {
        this.policyOMSType = policyOMSType;
    }

    public static class Attachment {
        private Long id;
        private Long areaId;
        private Long areaTypeCode;
        private LocalDate attachBeginDate;
        private Long moId;
        private Long muId;
        private String attachTypeCode;
        private String attachTypeName;
        private String processOfAttachment;
        private String processOfAttachmentName;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getAreaId() {
            return areaId;
        }

        public void setAreaId(Long areaId) {
            this.areaId = areaId;
        }

        public Long getAreaTypeCode() {
            return areaTypeCode;
        }

        public void setAreaTypeCode(Long areaTypeCode) {
            this.areaTypeCode = areaTypeCode;
        }

        public LocalDate getAttachBeginDate() {
            return attachBeginDate;
        }

        public void setAttachBeginDate(LocalDate attachBeginDate) {
            this.attachBeginDate = attachBeginDate;
        }

        public Long getMoId() {
            return moId;
        }

        public void setMoId(Long moId) {
            this.moId = moId;
        }

        public Long getMuId() {
            return muId;
        }

        public void setMuId(Long muId) {
            this.muId = muId;
        }

        public String getAttachTypeCode() {
            return attachTypeCode;
        }

        public void setAttachTypeCode(String attachTypeCode) {
            this.attachTypeCode = attachTypeCode;
        }

        public String getAttachTypeName() {
            return attachTypeName;
        }

        public void setAttachTypeName(String attachTypeName) {
            this.attachTypeName = attachTypeName;
        }

        public String getProcessOfAttachment() {
            return processOfAttachment;
        }

        public void setProcessOfAttachment(String processOfAttachment) {
            this.processOfAttachment = processOfAttachment;
        }

        public String getProcessOfAttachmentName() {
            return processOfAttachmentName;
        }

        public void setProcessOfAttachmentName(String processOfAttachmentName) {
            this.processOfAttachmentName = processOfAttachmentName;
        }
    }
}
