package moscow.ptnl.app.esu.aei.listener.processor;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import moscow.ptnl.app.esu.aei.listener.validator.AttachmentEventValidator;
import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.esu.EsuConsumerProcessor;
import moscow.ptnl.app.model.TopicType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.mos.emias.esu.model.AttachmentEvent;
import ru.mos.emias.esu.model.Event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * I_SCHR_1 - attachmentEventImportService - Получение сообщений из топика AttachmentEvent
 *
 * @author sorlov
 */
@Component
public class AttachmentEventProcessor extends EsuConsumerProcessor {

    @Autowired
    @Qualifier("objectMapperLocalTime")
    protected ObjectMapper mapper;

    @Autowired
    private AttachmentEventValidator attachmentEventValidator;

    @Override
    public TopicType getTopicType() {
        return TopicType.ATTACHMENT_EVENT;
    }

    @Override
    protected Optional<String> validate(String message) {
        //4.1. Система проверяет, что сообщение соответствует формату JSON и возможно произвести его парсинг
        Optional<String> errorMsg = attachmentEventValidator.validate(message);

        if (errorMsg.isEmpty()) {
            HashSet<String> errorFields = new HashSet<>();
            //4.2. Проводится форматно-логический контроль полученных данных на наличие обязательных атрибутов
            AttachmentEvent content;

            try {
                content = mapper
                        .readValue(message, AttachmentEvent.class);
            } catch (JsonProcessingException ex) {
                return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(ex.getMessage()));
            }
            errorFields.add(content.getAttachmentNewValue().getPatientId() == null || content.getAttachmentNewValue().getPatientId() < 0
                    ? "attachmentNewValue.patientId" : null);
            errorFields.add(content.getAttachmentNewValue().getAttachId() == null ? "attachmentNewValue.attachId" : null);
//            errorFields.add(content.getAttachmentNewValue().getAreaId() == null ? "attachmentNewValue.areaId" : null);
//            errorFields.add(content.getAttachmentNewValue().getAreaTypeCode() == null ? "attachmentNewValue.areaTypeCode" : null);
            errorFields.add(content.getAttachmentNewValue().getAttachBeginDate() == null ? "attachmentNewValue.attachBeginDate" : null);
            errorFields.add(content.getAttachmentNewValue().getMoId() == null ? "attachmentNewValue.moId" : null);
//            errorFields.add(content.getAttachmentNewValue().getMuId() == null ? "attachmentNewValue.muId" : null);
            errorFields.add(content.getAttachmentNewValue().getAttachType().getCode() == null ? "attachmentNewValue.attachType.code" : null);
            errorFields.add(content.getAttachmentNewValue().getAttachType().getTitle() == null ? "attachmentNewValue.attachType.title" : null);
//            errorFields.add(content.getAttachmentNewValue().getProcessOfAttachment() == null || content.getAttachmentNewValue().getProcessOfAttachment().getCode() == null ? "attachmentNewValue.processOfAttachment.code" : null);
//            errorFields.add(content.getAttachmentNewValue().getProcessOfAttachment() == null || content.getAttachmentNewValue().getProcessOfAttachment().getTitle() == null ? "attachmentNewValue.processOfAttachment.title" : null);
            errorFields.add(content.getEvent().getDateTime() == null ? "event.dateTime" : null);
            List<Event.EventType> eventTypes = Arrays.asList(Event.EventType.CREATE, Event.EventType.CLOSE, Event.EventType.CHANGE);
            errorFields.add(!eventTypes.contains(content.getEvent().getEventType()) ? "event.eventType" : null);

            errorFields.remove(null);

            if (!errorFields.isEmpty()) {
                return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(
                        "Пустое или некорректное значение полей: " + String.join(", ", errorFields)));
            }
        }
        return errorMsg;
    }
}
