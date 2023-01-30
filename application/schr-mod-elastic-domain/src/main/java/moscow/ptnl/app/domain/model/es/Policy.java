package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

public class Policy {

    @Field(type = FieldType.Keyword, name = "policyNumber")
    private String policyNumber;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis, name = "policyUpdateDate")
    private LocalDateTime policyUpdateDate;

    @Field(type = FieldType.Keyword, name = "policyStatus")
    private String policyStatus;

    @Field(type = FieldType.Keyword, name = "policyOMSType")
    private String policyOMSType;

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

    public String getPolicyOMSType() { return policyOMSType; }

    public void setPolicyOMSType(String policyOMSType) { this.policyOMSType = policyOMSType; }
}
