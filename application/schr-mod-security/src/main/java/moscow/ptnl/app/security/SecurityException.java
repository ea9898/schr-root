package moscow.ptnl.app.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Исключение безопасности типа UNAUTHORIZED_REQUEST_EXCEPTION.
 * 
 * @author mkachalov
 */
public class SecurityException extends RuntimeException {
    
    private final SecurityExceptionTypes type;
    
    /** Перечень идентификаторов недостающих Полномочий пользователя. */
    private List<Long> requiredRights;
    
    public SecurityException(SecurityExceptionTypes type) {
        this.type = type;
    }
    
    /**
     * 
     * @param type
     * @param requiredRights еречень идентификаторов недостающих Полномочий пользователя
     */
    public SecurityException(SecurityExceptionTypes type, Long ... requiredRights) {
        this.type = type;
        if (requiredRights != null && requiredRights.length > 0) {
            getRequiredRights().addAll(Arrays.asList(requiredRights));
        }
    }

    /** 
     * Перечень идентификаторов недостающих Полномочий пользователя.
     * @return  
     */
    public final List<Long> getRequiredRights() {
        if (requiredRights == null) {
            requiredRights = new ArrayList<>();
        }
        return requiredRights;
    }

    public SecurityExceptionTypes getType() {
        return type;
    }
    
}
