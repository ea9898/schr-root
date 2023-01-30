package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class Immunodiagnostics {

    @Field(type = FieldType.Nested, name = "immunodiagnostic", includeInParent = true)
    private List<Immunodiagnostic> immunodiagnostic;

    public List<Immunodiagnostic> getImmunodiagnostic() {
        return immunodiagnostic;
    }

    public void setImmunodiagnostic(List<Immunodiagnostic> immunodiagnostic) {
        this.immunodiagnostic = immunodiagnostic;
    }
}
