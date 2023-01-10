package model;

public class PatientConsents {
    private ConsentDetails consentDetails;

    public PatientConsents() {
    }

    public PatientConsents(ConsentDetails consentDetails) {
        this.consentDetails = consentDetails;
    }

    public ConsentDetails getConsentDetails() {
        return consentDetails;
    }

    public void setConsentDetails(ConsentDetails consentDetails) {
        this.consentDetails = consentDetails;
    }
}
