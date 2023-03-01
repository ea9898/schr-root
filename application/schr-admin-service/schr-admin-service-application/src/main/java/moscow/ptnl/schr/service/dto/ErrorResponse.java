package moscow.ptnl.schr.service.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * @author m.kachalov
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorResponse implements Serializable {

    //Описание ошибки
    private Error error;

    public ErrorResponse() {
    }

    public ErrorResponse(String code, String message) {
        this.error = new Error(code, message);
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

}
