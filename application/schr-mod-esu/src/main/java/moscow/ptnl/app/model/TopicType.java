package moscow.ptnl.app.model;

import java.util.Objects;
import java.util.Optional;

/**
 * Перечисление всех возможных входящих ЕСУ тем.
 *
 * @author sorlov
 */
public enum TopicType {

    ATTACHMENT_EVENT(1, "AttachmentEvent", PlannersEnum.I_SCHR_1)
    ;

    private final String topic;
    private final long id;
    private final PlannersEnum planner;

    TopicType(long id, String topic, PlannersEnum planner) {
        this.id = id;
        this.topic = topic;
        this.planner = planner;
    }

    public String getName() {
        return topic;
    }

    public long getId() {
        return id;
    }

    public PlannersEnum getPlanner() { return planner; }

    public static Optional<TopicType> find(String topicName) {
        for (TopicType topic : TopicType.values()) {
            if (Objects.equals(topic.topic, topicName)) {
                return Optional.of(topic);
            }
        }
        return Optional.empty();
    }
}
