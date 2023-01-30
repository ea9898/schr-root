package moscow.ptnl.app.esu;

import moscow.ptnl.app.config.PersistenceConstraint;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.domain.model.es.IndexEsuInput;
import moscow.ptnl.app.repository.es.IndexEsuInputRepository;
import moscow.ptnl.domain.entity.esu.EsuInput;
import moscow.ptnl.domain.entity.esu.EsuStatusType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import ru.mos.emias.esu.lib.consumer.message.EsuConsumerMessageProcessor;
import ru.mos.emias.esu.lib.consumer.message.EsuMessage;
import ru.mos.emias.esu.lib.exception.EsuConsumerDoNotRetryException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public abstract class EsuConsumerProcessor extends EsuConsumerMessageProcessor {

    protected static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    
    @PersistenceContext(unitName = PersistenceConstraint.PU_NAME)
    protected EntityManager entityManager;

    @Autowired
    protected TransactionTemplate transactions;

    @Autowired
    protected IndexEsuInputRepository indexEsuInputRepository;

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

            final IndexEsuInput indexEsuInput = new IndexEsuInput(esuMessage.getOffset(),
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(esuMessage.getTimestamp()), ZoneId.systemDefault()),
                    esuMessage.getKey(), esuMessage.getTopic(), esuMessage.getBody());
            String esId = transactions.execute((s) -> indexEsuInputRepository.save(indexEsuInput)).getId();

            final EsuInput input = new EsuInput(esId, esuMessage.getTopic(), LocalDateTime.now());

            errorMessage.ifPresent(s -> {
                input.setStatus(EsuStatusType.PROCESSED);
                input.setError(s);
            });
            transactions.executeWithoutResult((s) -> entityManager.persist(input));

            if (errorMessage.isPresent()) {
                throw new EsuConsumerDoNotRetryException(errorMessage.get());
            }
        } catch (EsuConsumerDoNotRetryException ex) {
            throw ex;
        } catch (Throwable th) {
            String error = String.format("Unexpected error while processing ESU message with key=%s, topic=%s",
                    esuMessage.getKey(), esuMessage.getTopic());
            LOG.error(error, th);
            throw new EsuConsumerDoNotRetryException(error, th);
        }
    }

    protected abstract Optional<String> validate(String message);
}
