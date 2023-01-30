package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

public class ConsentInfo {

    @Field(type = FieldType.Long, name = "consentId")
    private Long consentId;

    @Field(type = FieldType.Nested, name = "documentedConsent", includeInParent = true)
    private DocumentedConsent documentedConsent;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis, name = "issueDateTime")
    private LocalDateTime issueDateTime;

    @Field(type = FieldType.Long, name = "consentFormId")
    private Long consentFormId;

    @Field(type = FieldType.Long, name = "consentTypeId")
    private Long consentTypeId;

    @Field(type = FieldType.Long, name = "representativePhysicalId")
    private Long representativePhysicalId;

    @Field(type = FieldType.Long, name = "representativeLegalId")
    private Long representativeLegalId;

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

    public LocalDateTime getIssueDateTime() {
        return issueDateTime;
    }

    public void setIssueDateTime(LocalDateTime issueDateTime) {
        this.issueDateTime = issueDateTime;
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
