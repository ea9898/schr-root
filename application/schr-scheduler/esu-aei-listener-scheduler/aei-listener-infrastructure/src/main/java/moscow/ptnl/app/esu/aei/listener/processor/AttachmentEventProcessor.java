package moscow.ptnl.app.esu.aei.listener.processor;

import moscow.ptnl.app.esu.EsuConsumerProcessor;
import moscow.ptnl.app.model.TopicType;

import org.springframework.stereotype.Component;

/**
 * I_SCHR_1 - attachmentEventImportService - Получение сообщений из топика AttachmentEvent
 *
 * @author sorlov
 */
@Component
public class AttachmentEventProcessor extends EsuConsumerProcessor<Object> {

    @Override
    public TopicType getTopicType() {
        return TopicType.ATTACHMENT_EVENT;
    }

}
