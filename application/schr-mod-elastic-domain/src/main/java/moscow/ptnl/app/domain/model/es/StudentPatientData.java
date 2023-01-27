package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;
import org.springframework.data.annotation.Id;

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


}
