package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class ImmunoDrugsTns {

    @Field(type = FieldType.Long, name = "immunoDrugsTnCode")
    private List<Long> immunoDrugsTnCode;

    public List<Long> getImmunoDrugsTnCode() {
        return immunoDrugsTnCode;
    }

    public void setImmunoDrugsTnCode(List<Long> immunoDrugsTnCode) {
        this.immunoDrugsTnCode = immunoDrugsTnCode;
    }
}
