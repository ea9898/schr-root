package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class Immunodiagnostic {

    @Field(type = FieldType.Nested, name = "immunoDrugsTns", includeInParent = true)
    private ImmunoDrugsTns immunoDrugsTns;

    @Field(type = FieldType.Nested, name = "immunoTestKind", includeInParent = true)
    private ImmunoTestKind immunoTestKind;

    public ImmunoDrugsTns getImmunoDrugsTns() {
        return immunoDrugsTns;
    }

    public void setImmunoDrugsTns(ImmunoDrugsTns immunoDrugsTns) {
        this.immunoDrugsTns = immunoDrugsTns;
    }

    public ImmunoTestKind getImmunoTestKind() {
        return immunoTestKind;
    }

    public void setImmunoTestKind(ImmunoTestKind immunoTestKind) {
        this.immunoTestKind = immunoTestKind;
    }
}
