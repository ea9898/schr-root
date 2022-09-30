package moscow.ptnl.app.metrics;

import moscow.ptnl.app.metrics.bind.ServiceMetrics;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Вспомогательный класс для создания интерцептора считающего метрики при вызове 
 * бизнес методов.
 * 
 * @author mkachalov
 */
public class MetricsInterceptorHelper {
    
    private final static Logger LOG = LoggerFactory.getLogger(moscow.ptnl.app.metrics.MetricsInterceptorHelper.class);
    
    /**
     * Выполняет метод и собирает метрики по времени выполнения, счетчку ошибок 
     * и счетчику вызовов.
     * 
     * @param joinPoint
     * @param annotation
     * @param metrics
     * @return
     * @throws Throwable
     */
    public static Object executeMethod(ProceedingJoinPoint joinPoint, moscow.ptnl.app.metrics.Metrics annotation, ServiceMetrics metrics) throws Throwable {
        
        Method method = getMethod(joinPoint);
        
        Optional<ServiceMetrics.MethodMetrics> metrica = metrics.getMetrics(method);
        if (metrica.isPresent()) {
            metrica.get().getRequests().increment();
        }
        
        long method_start_time = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            //считаем ошибки
            if (metrica.isPresent()) {
                metrica.get().getErrors().increment();
            }
            throw e;
        } finally {
            if (metrica.isPresent()) {
                long method_execution_time = System.nanoTime() - method_start_time;
                metrica.get().getTimer().record(method_execution_time, TimeUnit.NANOSECONDS);
            }
        }
    }
    
    
    /**
     * Извлекает сигнатуру вызванного метода.
     * 
     * @param joinPoint
     * @return 
     */
    private static Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass().getDeclaredMethod(
                        joinPoint.getSignature().getName(),
                        method.getParameterTypes()
                );
            } catch (Exception e) {
                LOG.error("Ошибка получения метода", e);
            }
        }
        return method;
    }
    
}
