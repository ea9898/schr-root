package moscow.ptnl.app.last.anthropometry.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.esu.EsuConsumerProcessor;
import moscow.ptnl.app.last.anthropometry.validator.LastAnthropometryValidator;
import moscow.ptnl.app.model.TopicType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import ru.mos.emias.esu.model.Attribute;
import ru.mos.emias.esu.model.LastAnthropometry;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * I_SCHR_7 - Получение сообщений из топика LastAnthropometry
 *
 * @author sevgeniy
 */
@Component
public class LastAnthropometryProcessor extends EsuConsumerProcessor {

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private LastAnthropometryValidator lastAnthropometryValidator;

    @Override
    public TopicType getTopicType() {
        return TopicType.LAST_ANTHROPOMETRY;
    }

    @Override
    protected Optional<String> validate(String message) {
        //4.1. Система проверяет, что сообщение соответствует формату JSON и возможно произвести его парсинг
        Optional<String> errorMsg = lastAnthropometryValidator.validate(message);

        if (errorMsg.isEmpty()) {
            HashSet<String> errorFields = new HashSet<>();
            //4.2. Проводится форматно-логический контроль полученных данных на наличие обязательных атрибутов
            LastAnthropometry content;

            try {
                content = mapper.readValue(message, LastAnthropometry.class);
            } catch (JsonProcessingException ex) {
                return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(ex.getMessage()));
            }

            if (content.getPatientId() == null) {
                errorFields.add("lastAnthropometry.patientId");
            }

            boolean notEmptyContent = false;
            if (content.getMeasurements() != null && !content.getMeasurements().isEmpty()) {
                notEmptyContent = true;
                if (content.getMeasurements().stream().anyMatch(item -> item.getMeasurementValue() == null)) {
                    errorFields.add("lastAnthropometry.measurementValue");
                }
                if (content.getMeasurements().stream().anyMatch(item -> item.getMeasurementType() == null)) {
                    errorFields.add("lastAnthropometry.measurementType");
                }
                if (content.getMeasurements().stream().anyMatch(item -> item.getDocumentId() == null)) {
                    errorFields.add("lastAnthropometry.documentId");
                }
                if (content.getMeasurements().stream().anyMatch(item -> item.getMeasurementDate() == null)) {
                    errorFields.add("lastAnthropometry.measurementDate");
                }
            } else if (content.getAdditionalProperties() != null
                    && content.getAdditionalProperties().containsKey("annuledMeasurementsTypeId")) {
                notEmptyContent = true;
                if (content.getAdditionalProperties().values().isEmpty()) {
                    errorFields.add("lastAnthropometry.measurementValue");
                }
            }

            if (!notEmptyContent) {
                errorFields.add("lastAnthropometry.measurements или lastAnthropometry.additionalProperties");
            }

            if (!errorFields.isEmpty()) {
                return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(
                        "Пустое или некорректное значение полей: " + String.join(", ", errorFields)));
            }
        }
        return errorMsg;
    }
}
