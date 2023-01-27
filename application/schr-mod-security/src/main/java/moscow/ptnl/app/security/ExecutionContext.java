package moscow.ptnl.app.security;

import java.io.Serializable;
import java.util.UUID;

/**
 * Контекст запроса или периодической задачи.
 * 
 * @author m.kachalov
 */
public class ExecutionContext implements Serializable {
    
    private final String executionId; //произвольный уникальный идентификатор запроса или задачи

    /**
     * В конструкторе автоматически гененрируется уникальный идентификатор
     * выполняемой задачи (в формате UUID).
     */
    public ExecutionContext() {
        this.executionId = UUID.randomUUID().toString();
    }
    
    /**
     * В конструкторе назначается уникальный идентификатор
     * выполняемой задачи (желательно в формате UUID).
     * @param executionId 
     */
    public ExecutionContext(String executionId) {
        this.executionId = executionId;
    }
    
    /**
     * @return the executionId
     */
    public String getExecutionId() {
        return executionId;
    }
    
}
