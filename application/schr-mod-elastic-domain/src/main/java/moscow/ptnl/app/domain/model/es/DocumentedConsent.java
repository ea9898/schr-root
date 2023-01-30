package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

public class DocumentedConsent {

    @Field(type = FieldType.Boolean, name = "allMedicalIntervention")
    private Boolean allMedicalIntervention;

    @Field(type = FieldType.Keyword, name = "locationName")
    private String locationName;

    @Field(type = FieldType.Long, name = "cancelReasonId")
    private Long cancelReasonId;

    @Field(type = FieldType.Long, name = "locationId")
    private Long locationId;

    @Field(type = FieldType.Nested, name = "interventionDetails", includeInParent = true)
    private InterventionDetails interventionDetails;

    @Field(type = FieldType.Nested, name = "Immunodiagnostics", includeInParent = true)
    private Immunodiagnostics immunodiagnostics;

    @Field(type = FieldType.Keyword, name = "representativeDocumentId")
    private String representativeDocumentId;

    @Field(type = FieldType.Keyword, name = "cancelReasonOther")
    private String cancelReasonOther;

    @Field(type = FieldType.Keyword, name = "moName")
    private String moName;

    @Field(type = FieldType.Boolean, name = "signedByPatient")
    private Boolean signedByPatient;

    @Field(type = FieldType.Long, name = "moId")
    private Long moId;

    @Field(type = FieldType.Date, format = DateFormat.date, name = "createDate")
    private LocalDate createDate;

    public Boolean getAllMedicalIntervention() {
        return allMedicalIntervention;
    }

    public void setAllMedicalIntervention(Boolean allMedicalIntervention) {
        this.allMedicalIntervention = allMedicalIntervention;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Long getCancelReasonId() {
        return cancelReasonId;
    }

    public void setCancelReasonId(Long cancelReasonId) {
        this.cancelReasonId = cancelReasonId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
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

    public String getCancelReasonOther() {
        return cancelReasonOther;
    }

    public void setCancelReasonOther(String cancelReasonOther) {
        this.cancelReasonOther = cancelReasonOther;
    }

    public String getMoName() {
        return moName;
    }

    public void setMoName(String moName) {
        this.moName = moName;
    }

    public Boolean getSignedByPatient() {
        return signedByPatient;
    }

    public void setSignedByPatient(Boolean signedByPatient) {
        this.signedByPatient = signedByPatient;
    }

    public Long getMoId() {
        return moId;
    }

    public void setMoId(Long moId) {
        this.moId = moId;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }
}
