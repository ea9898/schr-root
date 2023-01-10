package model;

public class ImmunoTestKind {
    private Long immunoKindCode;
    private Long infectionCode;

    public ImmunoTestKind() {
    }

    public ImmunoTestKind(Long immunoKindCode, Long infectionCode) {
        this.immunoKindCode = immunoKindCode;
        this.infectionCode = infectionCode;
    }

    public Long getImmunoKindCode() {
        return immunoKindCode;
    }

    public void setImmunoKindCode(Long immunoKindCode) {
        this.immunoKindCode = immunoKindCode;
    }

    public Long getInfectionCode() {
        return infectionCode;
    }

    public void setInfectionCode(Long infectionCode) {
        this.infectionCode = infectionCode;
    }
}
