package moscow.ptnl.app.metrics.esu;

import moscow.ptnl.app.metrics.bind.EsuMetrics;
import ru.mos.emias.esu.lib.producer.EsuProducer;
import ru.mos.emias.esu.lib.producer.MessageMetadata;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author m.kachalov
 */
public class EsuProducerWrapper {
    
    private final EsuProducer producer;
    private final EsuMetrics metrics;
    private final boolean useMetrics;
    
    /**
     * Не используем кастомные (не esu-client-lib) метрики.
     * 
     * @param producer 
     */
    public EsuProducerWrapper(EsuProducer producer) {
        this.producer = producer;
        this.metrics = null;
        this.useMetrics = false;
    }
    
    /**
     * Используем кастомные метрики.
     * На метрики из esu-client-lib это не влияет.
     * 
     * @param producer
     * @param metrics 
     */
    public EsuProducerWrapper(EsuProducer producer, EsuMetrics metrics) {
        this.producer = producer;
        this.metrics = metrics;
        this.useMetrics = true;
    }
    
    private Optional<EsuCounter> getCounter(String topic) {
        return useMetrics 
                ? Optional.of(metrics.getMetricsOrError(topic)) 
                : Optional.empty();
    }
    
    public MessageMetadata publish(String topic, String message) throws InterruptedException, ExecutionException {
        Optional<EsuCounter> counter = getCounter(topic);
        try {
            counter.ifPresent(c -> c.getProducerRequests().increment());
            return producer.publish(topic, message);            
        } catch (Throwable e) {
            counter.ifPresent(c -> c.getProducerErrors().increment());
            throw e;
        }
    }

    public MessageMetadata publish(String topic, Object key, String message, Map<String, String> messageHeaders) throws ExecutionException, InterruptedException {
        Optional<EsuCounter> counter = getCounter(topic);
        try {
            counter.ifPresent(c -> c.getProducerRequests().increment());
            return producer.publish(topic, key, message, messageHeaders);
        } catch (Throwable e) {
            counter.ifPresent(c -> c.getProducerErrors().increment());
            throw e;
        }
    }

    public MessageMetadata publish(String topic, Object key, String message) throws ExecutionException, InterruptedException {
        Optional<EsuCounter> counter = getCounter(topic);
        try {
            counter.ifPresent(c -> c.getProducerRequests().increment());
            return producer.publish(topic, key, message);
        } catch (Throwable e) {
            counter.ifPresent(c -> c.getProducerErrors().increment());
            throw e;
        }
    }

    public void close() {
        producer.close();
    }

    public void beginTransaction() {
        producer.beginTransaction();
    }

    public void abortTransaction() {
       producer.abortTransaction();
    }

    public void commitTransaction() {
        producer.commitTransaction();
    }

    public void initTransactions() {
        producer.initTransactions();
    }

    public boolean isBrokerHasTopic(String topic, int timeout) {
        return producer.isBrokerHasTopic(topic, timeout);
    }
    
}
