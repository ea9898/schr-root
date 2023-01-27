package moscow.ptnl.app.patient.consents.topic.model;

public class PatientConsents {

    private Long patientId;

    private ConsentDetails consentDetails;

    public PatientConsents() {
    }

    public PatientConsents(Long patientId, ConsentDetails consentDetails) {
        this.patientId = patientId;
        this.consentDetails = consentDetails;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public ConsentDetails getConsentDetails() {
        return consentDetails;
    }

    public void setConsentDetails(ConsentDetails consentDetails) {
        this.consentDetails = consentDetails;
    }
}
