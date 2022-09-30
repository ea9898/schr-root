package moscow.ptnl.app.metrics.bind;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import moscow.ptnl.app.metrics.esu.EsuCounter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author m.kachalov
 */
public class EsuMetrics implements MeterBinder {
    
    private final Map<String, EsuCounter> metrics = new HashMap<>();
    
    /**
     * 
     * @param topics имена топиков в формате "ТОПИК.КЛИЕНТ_ID" 
     */
    public EsuMetrics(String ... topics) {
        this(Arrays.asList(topics));
    }
    
    /**
     * 
     * @param topics имена топиков в формате "ТОПИК.КЛИЕНТ_ID" 
     */
    public EsuMetrics(List<String> topics) {
        if (topics != null) {
            topics.forEach(topic -> addTopic(topic));
        }
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        for (EsuCounter m : metrics.values()) {
            m.setProducerRequests(registry.counter(m.getTopic() + "_producer_counter", m.tags()));
            m.setProducerErrors(registry.counter(m.getTopic() + "_producer_errors", m.tags()));
            m.setConsumerRequests(registry.counter(m.getTopic() + "_consumer_counter", m.tags()));
            m.setConsumerErrors(registry.counter(m.getTopic() + "_consumer_errors", m.tags()));
        }
    }
    
    public Optional<EsuCounter> getMetrics(String topic) {
        return Optional.ofNullable(metrics.get(topic));        
    }
    
    public EsuCounter getMetricsOrError(String topic) throws IllegalStateException {
        return getMetrics(topic)
                .orElseThrow(() -> new IllegalStateException("Незарегистрированный в метриках топик [" + topic + "]"));
    }
    
    private void addTopic(String topic) {
        if (!metrics.containsKey(topic)) {
            metrics.put(topic, new EsuCounter(topic));
        }
    }
    
}
