package model;

import java.time.LocalDate;

public class ConsentDetails {
    private Long consentId;
    private DocumentedConsent documentedConsent;
    private LocalDate issueDate;
    private Long consentFormId;
    private Long consentTypeId;
    private Long representativePhysicalId;
    private Long representativeLegalId;

    public ConsentDetails() {
    }

    public ConsentDetails(Long consentId, DocumentedConsent documentedConsent, LocalDate issueDate, Long consentFormId, Long consentTypeId,
                          Long representativePhysicalId, Long representativeLegalId) {
        this.consentId = consentId;
        this.documentedConsent = documentedConsent;
        this.issueDate = issueDate;
        this.consentFormId = consentFormId;
        this.consentTypeId = consentTypeId;
        this.representativePhysicalId = representativePhysicalId;
        this.representativeLegalId = representativeLegalId;
    }

    public Long getConsentId() {
        return consentId;
    }

    public void setConsentId(Long consentId) {
        this.consentId = consentId;
    }

    public DocumentedConsent getDocumentedConsent() {
        return documentedConsent;
    }

    public void setDocumentedConsent(DocumentedConsent documentedConsent) {
        this.documentedConsent = documentedConsent;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public Long getConsentFormId() {
        return consentFormId;
    }

    public void setConsentFormId(Long consentFormId) {
        this.consentFormId = consentFormId;
    }

    public Long getConsentTypeId() {
        return consentTypeId;
    }

    public void setConsentTypeId(Long consentTypeId) {
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
}
