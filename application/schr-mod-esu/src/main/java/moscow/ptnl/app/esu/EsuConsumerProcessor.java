package moscow.ptnl.app.esu;

import com.fasterxml.jackson.databind.ObjectMapper;

import moscow.ptnl.app.config.PersistenceConstraint;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.domain.entity.esu.EsuInput;
import moscow.ptnl.domain.entity.esu.EsuStatusType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import ru.mos.emias.esu.lib.consumer.message.EsuConsumerMessageProcessor;
import ru.mos.emias.esu.lib.consumer.message.EsuMessage;
import ru.mos.emias.esu.lib.exception.EsuConsumerDoNotRetryException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

public abstract class EsuConsumerProcessor extends EsuConsumerMessageProcessor {

    protected static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    
    @PersistenceContext(unitName = PersistenceConstraint.PU_NAME)
    protected EntityManager entityManager;

    @Autowired
    protected TransactionTemplate transactions;

    @Autowired
    protected ObjectMapper mapper;

    public abstract TopicType getTopicType();

    @Override
    public void process(EsuMessage esuMessage) throws EsuConsumerDoNotRetryException {
        LOG.info(String.format("Message received with key=%s, topic=%s", esuMessage.getKey(), esuMessage.getTopic()));
        final Optional<TopicType> topicType = TopicType.find(esuMessage.getTopic() == null ? null : esuMessage.getTopic().split("\\.")[0]);
        
        if (topicType.isEmpty() || !getTopicType().equals(topicType.get())) {
            throw new IllegalArgumentException("Имя топика в сообщении отстутствует или не соответствует имени топика в обработчике");
        }

        try {
            Optional<String> errorMessage = validate(esuMessage.getBody());
            //String esId = Elasticsearch.save(esuMessage.getBody());
            String esId = esuMessage.getBody(); //FIXME

            final EsuInput input = new EsuInput(esId, esuMessage.getTopic(), esuMessage.getBody(), LocalDateTime.now());

            errorMessage.ifPresent(s -> {
                input.setStatus(EsuStatusType.PROCESSED);
                input.setError(s);
            });
            transactions.executeWithoutResult((s) -> entityManager.persist(input));

            if (errorMessage.isPresent()) {
                throw new IllegalArgumentException(errorMessage.get());
            }
        } catch (Throwable th) {
            String error = String.format("Unexpected error while processing ESU message with key=%s, topic=%s",
                    esuMessage.getKey(), esuMessage.getTopic());
            LOG.error(error, th);
            throw new EsuConsumerDoNotRetryException(error, th);
        }
    }

    protected abstract Optional<String> validate(String message);
}
