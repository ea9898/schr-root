package moscow.ptnl.domain.entity.esu;

import moscow.ptnl.domain.converter.EsuStatusTypeConverter;
import moscow.ptnl.domain.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ESU_INPUT")
@SequenceGenerator(name = "SEQ_ESU_INPUT_ID", sequenceName = "SEQ_ESU_INPUT_ID", allocationSize = 1)
public class EsuInput extends BaseEntity<Long> implements Serializable {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SEQ_ESU_INPUT_ID")
    private Long id;

    @Column(name = "\"OFFSET\"", nullable = false)
    private Long offset;

    @Column(name = "TIMESTAMP", nullable = false)
    private LocalDateTime timeStamp;

    @Column(name = "KEY", nullable = false)
    private String key;

    @Column(name = "TOPIC", nullable = false)
    private String topic;

    @Column(name = "TEXT", nullable = false)
    private String text;

    @Column(name = "ERROR")
    private String error;

    @Column(name = "STATUS")
    @Convert(converter = EsuStatusTypeConverter.class)
    private EsuStatusType status;

    @Column(name = "CREATE_DATE", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "UPDATE_DATE", nullable = false)
    private LocalDateTime updateDate;

    public EsuInput() {
    }

    public EsuInput(Long offset, LocalDateTime timeStamp, String key, String topic, String text, LocalDateTime createDate) {
        this.offset = offset;
        this.timeStamp = timeStamp;
        this.key = key;
        this.topic = topic;
        this.text = text;
        this.createDate = createDate;
        this.updateDate = createDate;
        this.status = EsuStatusType.NEW;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public EsuStatusType getStatus() {
        return status;
    }

    public void setStatus(EsuStatusType status) {
        this.status = status;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}
