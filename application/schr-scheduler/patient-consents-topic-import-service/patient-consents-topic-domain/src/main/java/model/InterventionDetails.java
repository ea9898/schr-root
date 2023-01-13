package model;

import java.util.List;

public class InterventionDetails {
    private List<Integer> medInterventionId;

    public InterventionDetails() {
    }

    public InterventionDetails(List<Integer> interventionDetails) {
        this.medInterventionId = interventionDetails;
    }

    public List<Integer> getMedInterventionId() {
        return medInterventionId;
    }

    public void setMedInterventionId(List<Integer> medInterventionId) {
        this.medInterventionId = medInterventionId;
    }
}
