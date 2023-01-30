package moscow.ptnl.app.security.mapper;

import ru.mos.emias.errors.domain.OtherSecurityException;
import ru.mos.emias.errors.domain.UnauthorizedException;

/**
 * Интерфейс имплементируемый компонентом трансформирующим исключительную
 * ситуацию в исключение для SOAP или REST сервиса.
 * 
 * @author m.kachalov
 * @param <T>
 */
public interface UnauthorizedExceptionMapper<T extends Exception> {
    
    T map(UnauthorizedException exception, Class<T> faultClass);
    
    T map(OtherSecurityException e, Class<T> faultClass);
    
}
