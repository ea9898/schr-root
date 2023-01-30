package moscow.ptnl.schr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import moscow.ptnl.schr.error.ErrorReason;

/**
 *
 * @author m.kachalov
 */
public class DslQueryResult implements Serializable {
    
    private Long totalCount;
    private List<String> items;
    private String errorMessage;
    private ErrorReason reason;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<String> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setError(ErrorReason reason, String errorMessage) {
        this.reason = reason;
        this.errorMessage = errorMessage;
    }

    public ErrorReason getReason() {
        return reason;
    }
    
}
