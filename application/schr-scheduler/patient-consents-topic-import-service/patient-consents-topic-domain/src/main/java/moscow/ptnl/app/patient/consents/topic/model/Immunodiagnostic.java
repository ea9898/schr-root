package moscow.ptnl.app.patient.consents.topic.model;

public class Immunodiagnostic {
    private ImmunoTestKind immunoTestKind;
    private ImmunoDrugsTns immunoDrugsTns;

    public Immunodiagnostic() {
    }

    public Immunodiagnostic(ImmunoTestKind immunoTestKind, ImmunoDrugsTns immunoDrugsTns) {
        this.immunoTestKind = immunoTestKind;
        this.immunoDrugsTns = immunoDrugsTns;
    }

    public ImmunoTestKind getImmunoTestKind() {
        return immunoTestKind;
    }

    public void setImmunoTestKind(ImmunoTestKind immunoTestKind) {
        this.immunoTestKind = immunoTestKind;
    }

    public ImmunoDrugsTns getImmunoDrugsTns() {
        return immunoDrugsTns;
    }

    public void setImmunoDrugsTns(ImmunoDrugsTns immunoDrugsTns) {
        this.immunoDrugsTns = immunoDrugsTns;
    }
}
