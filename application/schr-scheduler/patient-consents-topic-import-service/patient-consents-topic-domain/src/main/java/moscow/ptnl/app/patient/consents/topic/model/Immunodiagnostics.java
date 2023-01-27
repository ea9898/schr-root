package moscow.ptnl.app.patient.consents.topic.model;

import java.util.List;

public class Immunodiagnostics {
    private List<Immunodiagnostic> immunodiagnostic;

    public Immunodiagnostics() {
    }

    public Immunodiagnostics(List<Immunodiagnostic> immunodiagnostic) {
        this.immunodiagnostic = immunodiagnostic;
    }

    public List<Immunodiagnostic> getImmunodiagnostic() {
        return immunodiagnostic;
    }

    public void setImmunodiagnostic(List<Immunodiagnostic> immunodiagnostic) {
        this.immunodiagnostic = immunodiagnostic;
    }
}
