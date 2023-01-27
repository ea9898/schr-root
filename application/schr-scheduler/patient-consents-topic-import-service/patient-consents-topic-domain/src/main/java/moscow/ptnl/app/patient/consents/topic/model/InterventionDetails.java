package moscow.ptnl.app.patient.consents.topic.model;

import java.util.List;

public class InterventionDetails {
    private List<Long> medInterventionId;

    public InterventionDetails() {
    }

    public InterventionDetails(List<Long> interventionDetails) {
        this.medInterventionId = interventionDetails;
    }

    public List<Long> getMedInterventionId() {
        return medInterventionId;
    }

    public void setMedInterventionId(List<Long> medInterventionId) {
        this.medInterventionId = medInterventionId;
    }
}
