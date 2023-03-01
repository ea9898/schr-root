package moscow.ptnl.schr.service.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import moscow.ptnl.app.rs.dto.UserContextRequest;

import java.io.Serializable;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SyncDataByPatientIdRequest extends UserContextRequest implements Serializable {

    private List<Long> patientId;
    private List<String> indexElement;

    public List<Long> getPatientId() {
        return patientId;
    }

    public void setPatientId(List<Long> patientId) {
        this.patientId = patientId;
    }

    public List<String> getIndexElement() {
        return indexElement;
    }

    public void setIndexElement(List<String> indexElement) {
        this.indexElement = indexElement;
    }
}
