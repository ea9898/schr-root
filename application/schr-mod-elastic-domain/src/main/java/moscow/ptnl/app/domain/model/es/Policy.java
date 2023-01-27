package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

public class Policy {

    @Field(type = FieldType.Keyword, name = "policyNumber")
    private String policyNumber;

    @Field(type = FieldType.Date, name = "policyUpdateDate")
    private LocalDateTime policyUpdateDate;

    @Field(type = FieldType.Keyword, name = "policyStatus")
    private String policyStatus;

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
}
