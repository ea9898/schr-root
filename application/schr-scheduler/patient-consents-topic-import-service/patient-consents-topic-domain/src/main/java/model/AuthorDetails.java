package model;

public class AuthorDetails {
    private String medEmployeeLogin;
    private String medEmployeeName;
    private String medEmployeeSpeciality;
    private Long muId;
    private String muName;
    private String host;

    public AuthorDetails() {
    }

    public AuthorDetails(String medEmployeeLogin, String medEmployeeName, String medEmployeeSpeciality, Long muId, String muName, String host) {
        this.medEmployeeLogin = medEmployeeLogin;
        this.medEmployeeName = medEmployeeName;
        this.medEmployeeSpeciality = medEmployeeSpeciality;
        this.muId = muId;
        this.muName = muName;
        this.host = host;
    }

    public String getMedEmployeeLogin() {
        return medEmployeeLogin;
    }

    public void setMedEmployeeLogin(String medEmployeeLogin) {
        this.medEmployeeLogin = medEmployeeLogin;
    }

    public String getMedEmployeeName() {
        return medEmployeeName;
    }

    public void setMedEmployeeName(String medEmployeeName) {
        this.medEmployeeName = medEmployeeName;
    }

    public String getMedEmployeeSpeciality() {
        return medEmployeeSpeciality;
    }

    public void setMedEmployeeSpeciality(String medEmployeeSpeciality) {
        this.medEmployeeSpeciality = medEmployeeSpeciality;
    }

    public Long getMuId() {
        return muId;
    }

    public void setMuId(Long muId) {
        this.muId = muId;
    }

    public String getMuName() {
        return muName;
    }

    public void setMuName(String muName) {
        this.muName = muName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
