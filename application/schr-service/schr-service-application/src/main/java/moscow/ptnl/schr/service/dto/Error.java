package moscow.ptnl.schr.service.dto;

import java.io.Serializable;

/**
 *
 * @author m.kachalov
 */
public class Error implements Serializable {
    
    private String code;
    private Object message;
    
    public Error(){}
    
    public Error(String code, Object message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
    
}
