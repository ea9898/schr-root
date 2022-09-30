package moscow.ptnl.app.metrics.interceptor;

import moscow.ptnl.app.metrics.MetricsInterceptorHelper;
import moscow.ptnl.app.metrics.bind.ServiceMetrics;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Обрабатывает методы сервиса аннотированные как @see Metrics 
 * и считает по ним статистику (метрики).
 * 
 * @author m.kachalov
 */
@Aspect 
@Order(0)
@Component
public class MetricsInterceptor {
    
    @Autowired
    private ServiceMetrics metrics;
    
    @Around(
        value = "execution(public * *(..)) && @annotation(annotation)",
        argNames="annotation"
    )
    public Object meter(ProceedingJoinPoint joinPoint, moscow.ptnl.app.metrics.Metrics annotation) throws Throwable {
               
        return MetricsInterceptorHelper.executeMethod(joinPoint, annotation, metrics);
        
    }
    
}
