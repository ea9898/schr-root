package moscow.ptnl.schr.service.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author m.kachalov
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorResponse implements Serializable {
    
    //Описание ошибки
    private Error error;
    //ИД ошибки в журнале запросов
    private String journalId;
    
    public ErrorResponse(){}
    
    public ErrorResponse(String code, String message, String journalId){
        this.error = new Error(code, message);
        this.journalId = journalId;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public String getJournalId() {
        return journalId;
    }

    public void setJournalId(String journalId) {
        this.journalId = journalId;
    }
    
}
