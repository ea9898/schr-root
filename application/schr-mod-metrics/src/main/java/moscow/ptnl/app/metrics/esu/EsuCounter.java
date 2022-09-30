package moscow.ptnl.app.metrics.esu;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author m.kachalov
 */
public class EsuCounter {
    
    private final String topic; //имя топика в формате "ТОПИК.КЛИЕНТ_ID"
    private Counter producerRequests; //счетчик числа сообщений отправленных продюсером (включая ошибки при отправке)
    private Counter producerErrors; //счетчик ошибок продюсера
    private Counter counsumerRequests; //считч числа сообщений полученных консумером (включая обработанные с ошибкой)
    private Counter consumerErrors; //счетчик ошибок консумера
    
    public EsuCounter(String topic) {
        this.topic = topic;
    }

    /**
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }
    
    public List<Tag> tags() {
        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("topic", getTopic()));
        return tags;
    }

    /**
     * @return the requests
     */
    public Counter getProducerRequests() {
        return producerRequests;
    }

    /**
     * @param requests the requests to set
     */
    public void setProducerRequests(Counter requests) {
        this.producerRequests = requests;
    }

    /**
     * @return the errors
     */
    public Counter getProducerErrors() {
        return producerErrors;
    }

    /**
     * @param errors the errors to set
     */
    public void setProducerErrors(Counter errors) {
        this.producerErrors = errors;
    }

    /**
     * @return the counsumerRequests
     */
    public Counter getCounsumerRequests() {
        return counsumerRequests;
    }

    /**
     * @param counsumerRequests the counsumerRequests to set
     */
    public void setConsumerRequests(Counter counsumerRequests) {
        this.counsumerRequests = counsumerRequests;
    }

    /**
     * @return the consumerErrors
     */
    public Counter getConsumerErrors() {
        return consumerErrors;
    }

    /**
     * @param consumerErrors the consumerErrors to set
     */
    public void setConsumerErrors(Counter consumerErrors) {
        this.consumerErrors = consumerErrors;
    }
    
}
