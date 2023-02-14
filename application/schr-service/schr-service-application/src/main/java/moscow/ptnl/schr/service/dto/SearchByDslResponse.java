package moscow.ptnl.schr.service.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author m.kachalov
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchByDslResponse implements Serializable {
    
    //Массив найденных документов в индексе
    private List<Object> items;
    //Общее количество найденных документов
    private Integer totalCount;

    public List<Object> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<Object> items) {
        this.items = items;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
    
}
