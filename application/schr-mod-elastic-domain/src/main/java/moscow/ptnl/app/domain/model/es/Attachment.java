package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Attachment {

    @Field(type = FieldType.Long, name = "id")
    private Long id;

    @Field(type = FieldType.Long, name = "areaId")
    private Long areaId;

    @Field(type = FieldType.Long, name = "areaTypeCode")
    private Long areaTypeCode;

    @Field(type = FieldType.Long, name = "attachBeginDate")
    private Long attachBeginDate;

    @Field(type = FieldType.Long, name = "moId")
    private Long moId;

    @Field(type = FieldType.Long, name = "muId")
    private Long muId;

    @Field(type = FieldType.Keyword, name = "attachTypeСode")
    private String attachTypeCode;

    @Field(type = FieldType.Keyword, name = "attachTypeName")
    private String attachTypeName;

    @Field(type = FieldType.Keyword, name = "processOfAttachmentCode")
    private String processOfAttachmentCode;

    @Field(type = FieldType.Keyword, name = "processOfAttachmentName")
    private String processOfAttachmentName;

    @Field(type = FieldType.Date, name = "updateDate")
    private LocalDateTime updateDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getAreaTypeCode() {
        return areaTypeCode;
    }

    public void setAreaTypeCode(Long areaTypeCode) {
        this.areaTypeCode = areaTypeCode;
    }

    public Long getAttachBeginDate() {
        return attachBeginDate;
    }

    public void setAttachBeginDate(Long attachBeginDate) {
        this.attachBeginDate = attachBeginDate;
    }

    public Long getMoId() {
        return moId;
    }

    public void setMoId(Long moId) {
        this.moId = moId;
    }

    public Long getMuId() {
        return muId;
    }

    public void setMuId(Long muId) {
        this.muId = muId;
    }

    public String getAttachTypeCode() {
        return attachTypeCode;
    }

    public void setAttachTypeCode(String attachTypeCode) {
        this.attachTypeCode = attachTypeCode;
    }

    public String getAttachTypeName() {
        return attachTypeName;
    }

    public void setAttachTypeName(String attachTypeName) {
        this.attachTypeName = attachTypeName;
    }

    public String getProcessOfAttachmentCode() {
        return processOfAttachmentCode;
    }

    public void setProcessOfAttachmentCode(String processOfAttachmentCode) {
        this.processOfAttachmentCode = processOfAttachmentCode;
    }

    public String getProcessOfAttachmentName() {
        return processOfAttachmentName;
    }

    public void setProcessOfAttachmentName(String processOfAttachmentName) {
        this.processOfAttachmentName = processOfAttachmentName;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}
