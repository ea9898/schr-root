package moscow.ptnl.app.metrics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для бизнес методов, по которым должна собираться статистика 
 * отображаемая в метриках. Для применения необходим также интерцептор
 * который будет обрабатывать аннотированные методы @see MetricsInterceptorHelper.
 * 
 * @author m.kachalov
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Metrics {
    
}
