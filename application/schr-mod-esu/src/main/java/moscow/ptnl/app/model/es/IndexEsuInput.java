package moscow.ptnl.app.model.es;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.time.LocalDateTime;

/**
 *
 * @author sorlov
 */
@Document(indexName = IndexEsuInput.INDEX_NAME)
public class IndexEsuInput {
    
    public static final String INDEX_NAME = "index_esu_input";

    @Id
    private String id;

    private Long offset;

    private LocalDateTime timeStamp;

    private String key;

    private String topic;

    private String message;

    @Field(name = "date_created")
    private LocalDateTime dateCreated;

    public IndexEsuInput() {
    }

    public IndexEsuInput(Long offset, LocalDateTime timeStamp, String key, String topic, String message) {
        this.offset = offset;
        this.timeStamp = timeStamp;
        this.key = key;
        this.topic = topic;
        this.message = message;
        this.dateCreated = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
