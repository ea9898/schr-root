package moscow.ptnl.schr.transform;

import moscow.ptnl.app.security.mapper.UnauthorizedExceptionMapper;
import moscow.ptnl.schr.error.ErrorReason;
import moscow.ptnl.schr.error.ServiceException;
import ru.mos.emias.errors.domain.OtherSecurityException;
import ru.mos.emias.errors.domain.UnauthorizedException;

import org.springframework.stereotype.Component;

/**
 * Реализация конвертера ошибок безопасности.
 *
 * @author m.kachalov
 */
@Component
public class UnauthorizedExceptionMapperImpl implements UnauthorizedExceptionMapper<ServiceException> {

    @Override
    public ServiceException map(UnauthorizedException exception, Class<ServiceException> faultClass) {
        return new ServiceException(ErrorReason.UNAUTHORIZED);
    }

    @Override
    public ServiceException map(OtherSecurityException e, Class<ServiceException> faultClass) {
        return new ServiceException(ErrorReason.UNAUTHORIZED);
    }

}
