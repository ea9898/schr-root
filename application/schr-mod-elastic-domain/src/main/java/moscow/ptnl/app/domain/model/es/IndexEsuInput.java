package moscow.ptnl.app.domain.model.es;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 *
 * @author sorlov
 */
@Document(indexName = IndexEsuInput.INDEX_NAME, createIndex = false)
public class IndexEsuInput {
    
    public static final String INDEX_NAME = "esu_input_alias";

    @Id
    private String id;

    @Field(type = FieldType.Long, name = "offset")
    private Long offset;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis, name = "timestamp")
    private LocalDateTime timeStamp;

    @Field(type = FieldType.Keyword, name = "key")
    private String key;

    @Field(type = FieldType.Keyword, name = "topic")
    private String topic;

    @Field(type = FieldType.Text, name = "message")
    private String message;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis, name = "date_created")
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
