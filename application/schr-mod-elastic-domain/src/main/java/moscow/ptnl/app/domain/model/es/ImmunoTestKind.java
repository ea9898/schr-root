package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class ImmunoTestKind {

    @Field(type = FieldType.Long, name = "infectionCode")
    private Long infectionCode;

    @Field(type = FieldType.Long, name = "immunoKindCode")
    private Long immunoKindCode;

    public Long getInfectionCode() {
        return infectionCode;
    }

    public void setInfectionCode(Long infectionCode) {
        this.infectionCode = infectionCode;
    }

    public Long getImmunoKindCode() {
        return immunoKindCode;
    }

    public void setImmunoKindCode(Long immunoKindCode) {
        this.immunoKindCode = immunoKindCode;
    }
}
