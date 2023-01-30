package moscow.ptnl.app.security;

/**
 * Типы исключений безопасности.
 * https://wiki.emias.mos.ru/display/EMIASSERVICES/SecurityExceptionTypes
 * 
 * @author mkachalov
 */
public enum SecurityExceptionTypes {
    
    /**
     * Тип исключения безопасности, вызванный недостаточностью Полномочий у 
     * пользователя для выполнения запрашиваемой операции.
     */
    UNAUTHORIZED_REQUEST_EXCEPTION,
    
    /**
     * Тип исключения безопасности, вызванный иными причинами.
     */
    OTHER_SECURITY_EXCEPTION;
    
}
