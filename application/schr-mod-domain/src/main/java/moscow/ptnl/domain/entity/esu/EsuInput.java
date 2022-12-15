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

    @Column(name = "ES_ID", unique = true, nullable = false)
    private String esId;

    @Column(name = "TOPIC", nullable = false)
    private String topic;

    @Column(name = "TEXT", nullable = false)
    private String text;

    @Column(name = "ERROR")
    private String error;

    @Column(name = "STATUS")
    @Convert(converter = EsuStatusTypeConverter.class)
    private EsuStatusType status;

    @Column(name = "DATE_CREATED", nullable = false)
    private LocalDateTime dateCreated;

    @Column(name = "DATE_UPDATED", nullable = false)
    private LocalDateTime dateUpdated;

    public EsuInput() {
    }

    public EsuInput(String esId, String topic, String text, LocalDateTime dateCreated) {
        this.esId = esId;
        this.topic = topic;
        this.text = text;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateCreated;
        this.status = EsuStatusType.NEW;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEsId() {
        return esId;
    }

    public void setEsId(String esId) {
        this.esId = esId;
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

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
