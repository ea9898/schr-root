package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class InterventionDetails {

    @Field(type = FieldType.Long, name = "medInterventionId")
    private List<Long> medInterventionId;

    public List<Long> getMedInterventionId() {
        return medInterventionId;
    }

    public void setMedInterventionId(List<Long> medInterventionId) {
        this.medInterventionId = medInterventionId;
    }
}
