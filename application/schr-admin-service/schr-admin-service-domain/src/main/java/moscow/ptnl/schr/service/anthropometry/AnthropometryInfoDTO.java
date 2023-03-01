package moscow.ptnl.schr.service.anthropometry;

import java.time.LocalDate;

public class AnthropometryInfoDTO {

    private LocalDate measurementDate;
    private String documentId;
    private int measurementType;
    private float measurementValue;
    private String centility;
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

    public int getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(int measurementType) {
        this.measurementType = measurementType;
    }

    public float getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(float measurementValue) {
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
