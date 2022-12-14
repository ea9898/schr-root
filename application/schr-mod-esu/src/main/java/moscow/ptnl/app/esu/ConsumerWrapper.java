package moscow.ptnl.app.esu;

import moscow.ptnl.app.model.TopicType;
import org.slf4j.Logger;
import ru.mos.emias.esu.lib.consumer.EsuTopicConsumer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sorlov
 */
public class ConsumerWrapper {

    private static final Set<ConsumerState> PAUSED_STATES = new HashSet<>(Arrays.asList(ConsumerState.PAUSED, ConsumerState.PAUSED_EXT));

    private ConsumerState state;
    private final EsuTopicConsumer consumer;
    private final String topicName;
    private final TopicType topic;
    private final Logger log;

    private Long entityId;

    public ConsumerWrapper(EsuTopicConsumer consumer, String topicName, TopicType topic, Logger log) {
        this.consumer = consumer;
        this.topic = topic;
        this.state = ConsumerState.NOT_ACTIVE;
        this.topicName = topicName;
        this.log = log;
    }

    public String getTopicName() {
        return topicName;
    }

    public TopicType getTopic() {
        return topic;
    }

    public Long getEntityId() {
        return entityId;
    }

    public boolean isDisabled() {
        return !ConsumerState.ACTIVE.equals(state);
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public boolean enable() throws Exception {
        return enable(false);
    }

    public synchronized boolean enable(boolean external) throws Exception {
        if (ConsumerState.NOT_ACTIVE.equals(state)) {
            this.consumer.consume();
            this.state = ConsumerState.ACTIVE;
            log.info("Запущен слушатель топика: {}", topicName);
            return true;
        }
        //Кто остановил слушателя, тот и может его перезапустить
        //Для избежания перезапуска планировщиком по расписанию, если поток был приостановлен из-за ошибок БД
        else if (external && ConsumerState.PAUSED_EXT.equals(state) || !external && ConsumerState.PAUSED.equals(state)) {
            this.consumer.proceed();
            this.state = ConsumerState.ACTIVE;
            log.info("Активирован слушатель топика: {}", topicName);
            return true;
        }
        return false;
    }

    public boolean disable() throws Exception {
        return disable(false);
    }

    public synchronized boolean disable(boolean external) throws Exception {
        if (ConsumerState.ACTIVE.equals(state)) {
            this.consumer.pause();
            this.state = external ? ConsumerState.PAUSED_EXT : ConsumerState.PAUSED;
            log.info("Приостановлен слушатель топика: {}", topicName);
            return true;
        }
        return false;
    }

    public boolean shutdown() throws Exception {
        boolean lastActive = ConsumerState.ACTIVE.equals(this.state) || PAUSED_STATES.contains(this.state);

        if (lastActive) {
            this.consumer.shutdown();
        }
        this.state = ConsumerState.NOT_ACTIVE;
        log.info("Завершен слушатель топика: {}", topicName);
        return lastActive;
    }
    
    /**
     * Состояние потребителя топика.
     */
    enum ConsumerState {
        NOT_ACTIVE,
        ACTIVE,
        PAUSED,
        PAUSED_EXT
    }
}
