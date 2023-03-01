package moscow.ptnl.schr.service.consent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ConsentInfoDTO {

    private long consentId;
    private DocumentedConsent documentedConsent;
    private LocalDateTime issueDate;
    private long consentFormId;
    private long consentTypeId;
    private Long representativePhysicalId;
    private Long representativeLegalId;

    public long getConsentId() {
        return consentId;
    }

    public void setConsentId(long consentId) {
        this.consentId = consentId;
    }

    public DocumentedConsent getDocumentedConsent() {
        return documentedConsent;
    }

    public void setDocumentedConsent(DocumentedConsent documentedConsent) {
        this.documentedConsent = documentedConsent;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public long getConsentFormId() {
        return consentFormId;
    }

    public void setConsentFormId(long consentFormId) {
        this.consentFormId = consentFormId;
    }

    public long getConsentTypeId() {
        return consentTypeId;
    }

    public void setConsentTypeId(long consentTypeId) {
        this.consentTypeId = consentTypeId;
    }

    public Long getRepresentativePhysicalId() {
        return representativePhysicalId;
    }

    public void setRepresentativePhysicalId(Long representativePhysicalId) {
        this.representativePhysicalId = representativePhysicalId;
    }

    public Long getRepresentativeLegalId() {
        return representativeLegalId;
    }

    public void setRepresentativeLegalId(Long representativeLegalId) {
        this.representativeLegalId = representativeLegalId;
    }

    public static class DocumentedConsent {
        private String documentId;
        private LocalDate createDate;
        private Long locationId;
        private String locationName;
        private boolean allMedicalIntervention;
        private List<Long> interventionDetails;
        private List<Immunodiagnostics> immunodiagnostics;
        private String representativeDocumentId;
        private Boolean signedByPatient;
        private long cancelReasonId;
        private String cancelReasonOther;
        private Long moId;
        private String moName;

        public String getDocumentId() {
            return documentId;
        }

        public void setDocumentId(String documentId) {
            this.documentId = documentId;
        }

        public LocalDate getCreateDate() {
            return createDate;
        }

        public void setCreateDate(LocalDate createDate) {
            this.createDate = createDate;
        }

        public Long getLocationId() {
            return locationId;
        }

        public void setLocationId(Long locationId) {
            this.locationId = locationId;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public boolean isAllMedicalIntervention() {
            return allMedicalIntervention;
        }

        public void setAllMedicalIntervention(boolean allMedicalIntervention) {
            this.allMedicalIntervention = allMedicalIntervention;
        }

        public List<Long> getInterventionDetails() {
            return interventionDetails;
        }

        public void setInterventionDetails(List<Long> interventionDetails) {
            this.interventionDetails = interventionDetails;
        }

        public List<Immunodiagnostics> getImmunodiagnostics() {
            return immunodiagnostics;
        }

        public void setImmunodiagnostics(List<Immunodiagnostics> immunodiagnostics) {
            this.immunodiagnostics = immunodiagnostics;
        }

        public String getRepresentativeDocumentId() {
            return representativeDocumentId;
        }

        public void setRepresentativeDocumentId(String representativeDocumentId) {
            this.representativeDocumentId = representativeDocumentId;
        }

        public Boolean getSignedByPatient() {
            return signedByPatient;
        }

        public void setSignedByPatient(Boolean signedByPatient) {
            this.signedByPatient = signedByPatient;
        }

        public long getCancelReasonId() {
            return cancelReasonId;
        }

        public void setCancelReasonId(long cancelReasonId) {
            this.cancelReasonId = cancelReasonId;
        }

        public String getCancelReasonOther() {
            return cancelReasonOther;
        }

        public void setCancelReasonOther(String cancelReasonOther) {
            this.cancelReasonOther = cancelReasonOther;
        }

        public Long getMoId() {
            return moId;
        }

        public void setMoId(Long moId) {
            this.moId = moId;
        }

        public String getMoName() {
            return moName;
        }

        public void setMoName(String moName) {
            this.moName = moName;
        }

        public static class Immunodiagnostics {

            private Long immunoKindCode;
            private Long infectionCode;
            private List<Long> immunoDrugsTnCodeList;

            public Long getImmunoKindCode() {
                return immunoKindCode;
            }

            public void setImmunoKindCode(Long immunoKindCode) {
                this.immunoKindCode = immunoKindCode;
            }

            public Long getInfectionCode() {
                return infectionCode;
            }

            public void setInfectionCode(Long infectionCode) {
                this.infectionCode = infectionCode;
            }

            public List<Long> getImmunoDrugsTnCodeList() {
                return immunoDrugsTnCodeList;
            }

            public void setImmunoDrugsTnCodeList(List<Long> immunoDrugsTnCodeList) {
                this.immunoDrugsTnCodeList = immunoDrugsTnCodeList;
            }

        }
    }
}
