package moscow.ptnl.app.esu.aei.listener.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moscow.ptnl.app.domain.model.es.Attachment;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.infrastructure.repository.es.StudentPatientDataRepository;
import moscow.ptnl.app.model.PlannersEnum;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.task.BaseEsuProcessorTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.mos.emias.esu.model.AttachmentEvent;
import ru.mos.emias.esu.model.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

/**
 * I_SCHR_2 - Обработка сообщений топика AttachmentEvent
 * 
 * @author sorlov
 */
@Component
@PropertySource("classpath:esu.properties")
public class AttachmentEventProcessTask extends BaseEsuProcessorTask {

    @Autowired
    @Qualifier("objectMapperLocalTime")
    protected ObjectMapper mapper;

    @Autowired
    private StudentPatientDataRepository studentPatientDataRepository;

    @Override
    protected Optional<String> processMessage(String inputMsg) {
        //4.2 Система выполняет парсинг сообщения
        AttachmentEvent content;

        try {
            content = mapper.readValue(inputMsg, AttachmentEvent.class);
        } catch (JsonProcessingException ex) {
            return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(ex.getMessage()));
        }
        //4.3 Система проверяет значение типа участка для полученного прикрепления
        if (!Arrays.asList(10L, 20L).contains(content.getAttachmentNewValue().getAreaTypeCode())) {
            return Optional.of(CustomErrorReason.INCORRECT_ATTACHMENT_TYPE.format());
        }
        return transactions.execute(s -> {
            //4.4 Поиск документа в индексе student_patient_registry
            Optional<StudentPatientData> studentPatientData = studentPatientDataRepository.findById(
                    content.getAttachmentNewValue().getPatientId().toString());

            if (studentPatientData.isEmpty()
                    || studentPatientData.get().getPatientInfo() == null
                    || !Objects.equals(studentPatientData.get().getId(), String.valueOf(studentPatientData.get().getPatientInfo().getPatientId()))) {
                return Optional.of(CustomErrorReason.PATIENT_NOT_FOUND.format(content.getAttachmentNewValue().getPatientId()));
            }
            //4.5 Для документа найденного на шаге 4.4, выполняется поиск блоков в элементе индекса attachments
            boolean eventTypeClose = Event.EventType.CLOSE.equals(content.getEvent().getEventType());
            boolean attachmentsFound = false;
            Iterator<Attachment> attachments = studentPatientData.get().getAttachments().listIterator();

            while (attachments.hasNext()) {
                Attachment attachment = attachments.next();

                if (Objects.equals(attachment.getAreaId(), content.getAttachmentNewValue().getAreaId())) {
                    attachmentsFound = true;

                    if (!content.getEvent().getDateTime().isAfter(attachment.getUpdateDate())) {
                        return Optional.of(CustomErrorReason.INFORMATION_IS_OUTDATED.format());
                    }
                    attachments.remove();
                }
            }
            if (studentPatientData.get().getAttachments().isEmpty()) {
                studentPatientData.get().setAttachments(null);
            }
            if (!attachmentsFound && eventTypeClose) {
                return Optional.of(CustomErrorReason.REMOVING_RECORD_NOT_FOUND.format());
            }
            if (!eventTypeClose) {
                //4.6 Система записывает блок в элемент индекса attachments
                applyData(content, studentPatientData.get());
            }
            studentPatientDataRepository.save(studentPatientData.get());

            return Optional.empty();
        });
    }

    private void applyData(AttachmentEvent newData, StudentPatientData entity) {
        Attachment attachment = new Attachment();
        attachment.setId(newData.getAttachmentNewValue().getAttachId());
        attachment.setAreaId(newData.getAttachmentNewValue().getAreaId());
        attachment.setAreaTypeCode(newData.getAttachmentNewValue().getAreaTypeCode());
        attachment.setAttachBeginDate(newData.getAttachmentNewValue().getAttachBeginDate());
        attachment.setMoId(newData.getAttachmentNewValue().getMoId());
        attachment.setMuId(newData.getAttachmentNewValue().getMuId());

        if (newData.getAttachmentNewValue().getAttachType() != null) {
            attachment.setAttachTypeCode(newData.getAttachmentNewValue().getAttachType().getCode());
            attachment.setAttachTypeName(newData.getAttachmentNewValue().getAttachType().getTitle());
        }
        if (newData.getAttachmentNewValue().getProcessOfAttachment() != null) {
            attachment.setProcessOfAttachmentCode(newData.getAttachmentNewValue().getProcessOfAttachment().getCode());
            attachment.setProcessOfAttachmentName(newData.getAttachmentNewValue().getProcessOfAttachment().getTitle());
        }
        attachment.setUpdateDate(newData.getEvent().getDateTime());

        if (entity.getAttachments() == null) {
            entity.setAttachments(new ArrayList<>());
        }
        entity.getAttachments().add(attachment);
    }

    @Override
    protected final PlannersEnum getPlanner() {
        return PlannersEnum.I_SCHR_2;
    }

    @Override
    protected TopicType getTopic() {
        return TopicType.ATTACHMENT_EVENT;
    }
}
