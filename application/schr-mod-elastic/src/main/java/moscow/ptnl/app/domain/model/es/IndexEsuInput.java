package moscow.ptnl.app.domain.model.es;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.time.LocalDateTime;

/**
 *
 * @author sorlov
 */
@Document(indexName = IndexEsuInput.INDEX_NAME, createIndex = false)
public class IndexEsuInput {
    
    public static final String INDEX_NAME = "index_esu_input";
    
    private Long offset;

    private LocalDateTime timeStamp;

    private String key;

    private String topic;

    private String message;

    @Field(name = "date_created")
    private LocalDateTime dateCreated;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
}
