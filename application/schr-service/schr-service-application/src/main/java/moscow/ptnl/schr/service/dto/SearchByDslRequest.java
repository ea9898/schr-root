package moscow.ptnl.schr.service.dto;

import moscow.ptnl.app.rs.dto.UserContextRequest;
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
public class SearchByDslRequest extends UserContextRequest implements Serializable {
    
    //Текст DSL-запроса
    private Object dslQuery;

    public Object getDslQuery() {
        return dslQuery;
    }

    public void setDslQuery(Object dslQuery) {
        this.dslQuery = dslQuery;
    }
    
}
