package model;

import java.time.LocalDate;

public class DocumentedConsent {
    private String documentId;
    private LocalDate createDate;
    private Long locationId;
    private String locationName;
    private Boolean allMedicalIntervention;
    private InterventionDetails interventionDetails;
    private Immunodiagnostics immunodiagnostics;
    private String representativeDocumentId;
    private Boolean signedByPatient;
    private Long cancelReasonId;
    private String cancelReasonOther;
    private Long moId;
    private String moName;

    public DocumentedConsent() {
    }

    public DocumentedConsent(String documentId, LocalDate createDate, Long locationId, String locationName,
                             Boolean allMedicalIntervention, InterventionDetails interventionDetails,
                             Immunodiagnostics immunodiagnostics, String representativeDocumentId,
                             Boolean signedByPatient, Long cancelReasonId, String cancelReasonOther,
                             Long moId, String moName) {
        this.documentId = documentId;
        this.createDate = createDate;
        this.locationId = locationId;
        this.locationName = locationName;
        this.allMedicalIntervention = allMedicalIntervention;
        this.interventionDetails = interventionDetails;
        this.immunodiagnostics = immunodiagnostics;
        this.representativeDocumentId = representativeDocumentId;
        this.signedByPatient = signedByPatient;
        this.cancelReasonId = cancelReasonId;
        this.cancelReasonOther = cancelReasonOther;
        this.moId = moId;
        this.moName = moName;
    }

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

    public Boolean getAllMedicalIntervention() {
        return allMedicalIntervention;
    }

    public void setAllMedicalIntervention(Boolean allMedicalIntervention) {
        this.allMedicalIntervention = allMedicalIntervention;
    }

    public InterventionDetails getInterventionDetails() {
        return interventionDetails;
    }

    public void setInterventionDetails(InterventionDetails interventionDetails) {
        this.interventionDetails = interventionDetails;
    }

    public Immunodiagnostics getImmunodiagnostics() {
        return immunodiagnostics;
    }

    public void setImmunodiagnostics(Immunodiagnostics immunodiagnostics) {
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

    public Long getCancelReasonId() {
        return cancelReasonId;
    }

    public void setCancelReasonId(Long cancelReasonId) {
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
}
