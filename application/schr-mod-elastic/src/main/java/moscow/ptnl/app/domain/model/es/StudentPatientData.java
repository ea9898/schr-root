package moscow.ptnl.app.domain.model.es;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

import static org.springframework.data.elasticsearch.annotations.FieldType.Nested;

@Document(indexName = StudentPatientData.INDEX_NAME, createIndex = false)
public class StudentPatientData {

    public static final String INDEX_NAME = "student_patient_registry_alias";

    @Id
    private String id;

    @Field(type = Nested, name = "patientInfo", includeInParent = true)
    private PatientInfo patientInfo;

    @Field(type = Nested, name = "anthropometryInfo", includeInParent = true)
    private List<AnthropometryInfo> anthropometryInfo;

    @Field(type = Nested, name = "attachments", includeInParent = true)
    private List<Attachment> attachments;

    @Field(type = Nested, name = "policy", includeInParent = true)
    private Policy policy;

    @Field(type = Nested, name = "studInfo", includeInParent = true)
    private List<StudentAttachInfo> studInfo;

    @Field(type = Nested, name = "сonsentsInfo", includeInParent = true)
    private List<ConsentsInfo> сonsentsInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PatientInfo getPatientInfo() {
        return patientInfo;
    }

    public void setPatientInfo(PatientInfo patientInfo) {
        this.patientInfo = patientInfo;
    }

    public List<AnthropometryInfo> getAnthropometryInfo() {
        return anthropometryInfo;
    }

    public void setAnthropometryInfo(List<AnthropometryInfo> anthropometryInfo) {
        this.anthropometryInfo = anthropometryInfo;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public List<StudentAttachInfo> getStudInfo() {
        return studInfo;
    }

    public void setStudInfo(List<StudentAttachInfo> studInfo) {
        this.studInfo = studInfo;
    }

    public List<ConsentsInfo> getСonsentsInfo() {
        return сonsentsInfo;
    }

    public void setСonsentsInfo(List<ConsentsInfo> сonsentsInfo) {
        this.сonsentsInfo = сonsentsInfo;
    }
}
