package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

public class AnthropometryInfo {

    @Field(type = FieldType.Date, name = "measurementDate")
    private LocalDate measurementDate;

    @Field(type = FieldType.Keyword, name = "documentId")
    private String documentId;

    @Field(type = FieldType.Integer, name = "measurementType")
    private Integer measurementType;

    @Field(type = FieldType.Float, name = "measurementValue")
    private Float measurementValue;

    @Field(type = FieldType.Keyword, name = "centility")
    private String centility;

    @Field(type = FieldType.Long, name = "resultAssessmentId")
    private Long resultAssessmentId;

    public LocalDate getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(LocalDate measurementDate) {
        this.measurementDate = measurementDate;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Integer getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(Integer measurementType) {
        this.measurementType = measurementType;
    }

    public Float getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(Float measurementValue) {
        this.measurementValue = measurementValue;
    }

    public String getCentility() {
        return centility;
    }

    public void setCentility(String centility) {
        this.centility = centility;
    }

    public Long getResultAssessmentId() {
        return resultAssessmentId;
    }

    public void setResultAssessmentId(Long resultAssessmentId) {
        this.resultAssessmentId = resultAssessmentId;
    }
}
