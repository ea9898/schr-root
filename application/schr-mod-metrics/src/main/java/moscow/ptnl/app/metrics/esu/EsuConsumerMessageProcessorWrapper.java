package moscow.ptnl.app.metrics.esu;

import moscow.ptnl.app.metrics.bind.EsuMetrics;
import ru.mos.emias.esu.lib.consumer.message.EsuConsumerMessageProcessor;
import ru.mos.emias.esu.lib.consumer.message.EsuMessage;

/**
 *
 * @author m.kachalov
 */
public abstract class EsuConsumerMessageProcessorWrapper extends EsuConsumerMessageProcessor {
    
    private final EsuMetrics metrics;
    
    public EsuConsumerMessageProcessorWrapper(EsuMetrics metrics) {
        this.metrics = metrics;
    }
    
    /**
     * Метод который делает что то полезное при обработке полученного
     * консюмером сообщения.
     * 
     * @param em
     * @throws Exception 
     */
    public abstract void handle(EsuMessage em) throws Exception;

    @Override
    public void process(EsuMessage em) throws Exception {
        EsuCounter counter = metrics.getMetricsOrError(em.getTopic());
        try {
            counter.getCounsumerRequests().increment();
            handle(em);
        } catch (Throwable e) {
            counter.getConsumerErrors().increment();
            throw e;
        }
    }
    
}
