package moscow.ptnl.app.metrics.bind;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Статистика по количеству запросов и времени выполнения методов аннотированных
 * как @see Metrics.
 * 
 * @author m.kachalov
 */
public class ServiceMetrics implements MeterBinder {
    
    private final Set<MethodMetrics> methods;
    
    /**
     * 
     * @param serviceClasses классы в которых ищутся аннотированные методы
     */
    public ServiceMetrics(Class<?>... serviceClasses){
        this.methods = new HashSet<>();
        for(Class<?> clazz : serviceClasses) {
            this.methods.addAll(getMethods(clazz));
        }
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        for (MethodMetrics m : methods) {
            m.setRequests(registry.counter(m.getName() + "_counter", m.tags()));
            m.setTimer(registry.timer(m.getName() + "_time", m.tags()));
            m.setErrors(registry.counter(m.getName() + "_errors", m.tags()));
        }
    }
    
    public Optional<MethodMetrics> getMetrics(Method method) {
        for (MethodMetrics m : this.methods) {
            if (m.getMethod().equals(method)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Методы класса аннотированные как @see Metrics.
     * 
     * @param clazz
     * @return 
     */
    private Set<MethodMetrics> getMethods(Class<?> clazz) {
        Set<MethodMetrics> methods = new HashSet<>();
        for (Method m : clazz.getMethods()) {
            if (m.getAnnotation(moscow.ptnl.app.metrics.Metrics.class) != null) {
                methods.add(new MethodMetrics(m));
            }
        }
        return methods;
    }
    
    public static class MethodMetrics {
        
        private final Method method;
        private Counter requests; //счетчик числа запросов
        private Timer timer; //диаграма времени отклика
        private Counter errors;

        public MethodMetrics(Method method) {
            this.method = method;
        }
        
        public String getName() {
            return getMethod().getName();
        }
        
        public List<Tag> tags() {
            List<Tag> tags = new ArrayList<>();
            tags.add(Tag.of("class", getMethod().getDeclaringClass().getName()));
            return tags;
        }

        /**
         * @return the counter
         */
        public Counter getRequests() {
            return requests;
        }

        /**
         * @param counter the counter to set
         */
        public void setRequests(Counter counter) {
            this.requests = counter;
        }

        /**
         * @return the method
         */
        public Method getMethod() {
            return method;
        }

        /**
         * @return the timer
         */
        public Timer getTimer() {
            return timer;
        }

        /**
         * @param timer the timer to set
         */
        public void setTimer(Timer timer) {
            this.timer = timer;
        }

        /**
         * @return the errors
         */
        public Counter getErrors() {
            return errors;
        }

        /**
         * @param errors the errors to set
         */
        public void setErrors(Counter errors) {
            this.errors = errors;
        }
        
        
        
    }
    
}
