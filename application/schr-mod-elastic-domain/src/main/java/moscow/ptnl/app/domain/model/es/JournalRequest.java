package moscow.ptnl.app.domain.model.es;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Индекс "Журнал с историей запросов к методам".
 * 
 * @author m.kachalov
 */
@Document(indexName = JournalRequest.INDEX_NAME, createIndex = false)
public class JournalRequest {
    
    public static final String INDEX_NAME = "journal_requests_alias";
    
    @Id
    private String id; 
    @Field(type = FieldType.Keyword)
    private String uuid;
    @Field(type = FieldType.Keyword)
    private String uri;
    @Field(type = FieldType.Keyword, name="system_name")
    private String systemName;
    @Field(type = FieldType.Keyword, name="user_name")
    private String userName;
    @Field(name="request", type = FieldType.Text)
    private String restRequest;
    @Field(name="http_status_code", type = FieldType.Short)
    private short httpStatusCode;
    @Field(name="error_message", type = FieldType.Text)
    private String errorMessage;
    @Field(name="date_request", type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime requestDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRestRequest() {
        return restRequest;
    }

    public void setRestRequest(String restRequest) {
        this.restRequest = restRequest;
    }

    public short getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(short httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }
    
}
