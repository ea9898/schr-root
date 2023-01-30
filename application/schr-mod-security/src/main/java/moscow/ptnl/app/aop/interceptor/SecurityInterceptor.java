package moscow.ptnl.app.aop.interceptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import moscow.ptnl.app.security.ExecutionContextHolder;
import moscow.ptnl.app.security.mapper.UnauthorizedExceptionMapper;
import moscow.ptnl.app.security.service.SecuritySettingService;
import moscow.ptnl.app.security.setting.AuthMethod;
import moscow.ptnl.app.security.setting.AuthService;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.mos.emias.errors.domain.UnauthorizedException;

/**
 * Проверяет наличие прав у пользователя на доступ к методу аннотированному
 * как @see EMIASSecured с учетом настроек из AD_CONFIG.
 * 
 * @author mkachalov
 */
@Aspect @Order(1)
@Component
public class SecurityInterceptor {
  
    private final static Logger LOG = LoggerFactory.getLogger(SecurityInterceptor.class);
    
    @Autowired
    private SecuritySettingService securitySettings;

    @Autowired
    private UnauthorizedExceptionMapper exceptionMapper;

    @Around(
        value = "execution(public * *(..)) && @annotation(annotation)",
        argNames="annotation"
    )
    public Object secured(ProceedingJoinPoint joinPoint, moscow.ptnl.app.security.annotation.EMIASSecured annotation) throws Throwable {

        String serviceName = getServiceName(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();
        
        LOG.debug("Защищенный метод: {} в сервисе: {}", methodName, serviceName);
        
        Optional<AuthService> setting = securitySettings.getSecuritySettings(serviceName);
        if (setting.isPresent()) {
            AuthService serviceSetting = setting.get();
            if (serviceSetting.isEnabled()) { //включена проверка авторизации для сервиса
                Map<String, AuthMethod> methodSetting = serviceSetting.getAuthMethods();
                if (!methodSetting.containsKey(methodName)) {
                    LOG.warn("В настройках ограничений доступа для сервиса: {}, метод: {} не перечислен", serviceName, methodName);
                    throw exceptionMapper.map(
                            new UnauthorizedException(
                                methodSetting.get(methodName) != null ? methodSetting.get(methodName).getAccessPermissions() : new long[]{0}
                            ),
                            annotation.faultClass()
                    );
                } else if (methodSetting.get(methodName).isEnabled()) { //включена проверка авторизации для метода
                    if (!hasAcessRights(methodSetting.get(methodName).getPermissions(), ExecutionContextHolder.getUserContext().getUserRights())) {
                        throw exceptionMapper.map(
                                new UnauthorizedException(
                                        methodSetting.get(methodName).getAccessPermissions()
                                ), 
                                annotation.faultClass()
                        );
                    }
                }
            }            
        }
        
        return joinPoint.proceed();
    }
    
    private String getServiceName(Class<?> clazz) {
        Service serviceAnnotation = clazz.getAnnotation(Service.class);
        if (serviceAnnotation == null) {
            throw new RuntimeException("У сервиса не определено имя");
        }
        return serviceAnnotation.value().split("-")[0];
    }
    
    private boolean hasAcessRights(List<Long> methodRights, List<Long> userRights) {
        return methodRights.stream().anyMatch(userRights::contains);
    }
    
}
