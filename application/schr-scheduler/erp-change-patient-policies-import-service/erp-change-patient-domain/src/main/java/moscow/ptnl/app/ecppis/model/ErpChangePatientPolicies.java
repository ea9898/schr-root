package moscow.ptnl.app.ecppis.model;

import java.time.LocalDateTime;
import java.util.List;

public class ErpChangePatientPolicies {
    private Integer id;
    private LocalDateTime operationDate;
    private Integer emiasId;
    private String uklErp;
    private String patientType;
    private String patientRecStatus;
    private String entityName;
    private List<EntityData> entityData;

    public ErpChangePatientPolicies() {
    }

    public ErpChangePatientPolicies(Integer id, LocalDateTime operationDate, Integer emiasId, String uklErp,
                                    String patientType, String patientRecStatus, String entityName, List<EntityData> entityData) {
        this.id = id;
        this.operationDate = operationDate;
        this.emiasId = emiasId;
        this.uklErp = uklErp;
        this.patientType = patientType;
        this.patientRecStatus = patientRecStatus;
        this.entityName = entityName;
        this.entityData = entityData;
    }

    public List<EntityData> getEntityData() {
        return entityData;
    }

    public void setEntityData(List<EntityData> entityData) {
        this.entityData = entityData;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmiasId() {
        return emiasId;
    }

    public void setEmiasId(Integer emiasId) {
        this.emiasId = emiasId;
    }

    public LocalDateTime getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(LocalDateTime operationDate) {
        this.operationDate = operationDate;
    }

    public String getUklErp() {
        return uklErp;
    }

    public void setUklErp(String uklErp) {
        this.uklErp = uklErp;
    }

    public String getPatientType() {
        return patientType;
    }

    public void setPatientType(String patientType) {
        this.patientType = patientType;
    }

    public String getPatientRecStatus() {
        return patientRecStatus;
    }

    public void setPatientRecStatus(String patientRecStatus) {
        this.patientRecStatus = patientRecStatus;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}
