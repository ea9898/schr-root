package moscow.ptnl.app.la.handler.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import moscow.ptnl.app.domain.model.es.AnthropometryInfo;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.infrastructure.repository.es.StudentPatientDataRepository;
import moscow.ptnl.app.model.PlannersEnum;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.task.BaseEsuProcessorTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.mos.emias.esu.model.LastAnthropometry;
import ru.mos.emias.esu.model.Measurement;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * I_SCHR_8 - Обработка сообщений топика LastAnthropometry
 * 
 * @author sorlov
 */
@Component
@PropertySource("classpath:esu.properties")
public class LastAnthropometryProcessTask extends BaseEsuProcessorTask {

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private StudentPatientDataRepository studentPatientDataRepository;

    @Override
    protected Optional<String> processMessage(String inputMsg) {
        //4.2 Система выполняет парсинг сообщения
        LastAnthropometry content;

        try {
            content = mapper.readValue(inputMsg, LastAnthropometry.class);
        } catch (JsonProcessingException ex) {
            return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(ex.getMessage()));
        }
        return transactions.execute(s -> {
            String error = null;
            //4.3 Поиск документа в индексе student_patient_registry
            Optional<StudentPatientData> studentPatientData = studentPatientDataRepository.findById(content.getPatientId());

            if (studentPatientData.isEmpty()
                    || studentPatientData.get().getPatientInfo() == null
                    || !Objects.equals(studentPatientData.get().getId(), String.valueOf(studentPatientData.get().getPatientInfo().getPatientId()))) {
                return Optional.of(CustomErrorReason.PATIENT_NOT_FOUND.format(content.getPatientId()));
            }
            if (content.getMeasurements() != null && !content.getMeasurements().isEmpty()) {
                //4.5 Добавление нового блока в элемент индекса существующего документа
                for (Measurement measurement : content.getMeasurements()) {
                    boolean needToAddMeasurement = true;
                    Iterator<AnthropometryInfo> anthropometry = studentPatientData.get().getAnthropometryInfo().listIterator();

                    while (anthropometry.hasNext()) {
                        AnthropometryInfo anthropometryInfo = anthropometry.next();

                        if (Objects.equals(anthropometryInfo.getMeasurementType(), measurement.getMeasurementType().intValue())) {
                            if (anthropometryInfo.getMeasurementDate().isBefore(measurement.getMeasurementDate())) {
                                anthropometry.remove();
                            } else {
                                error = CustomErrorReason.INFORMATION_IS_OUTDATED.format();
                                needToAddMeasurement = false;
                            }
                        }
                    }
                    if (needToAddMeasurement) {
                        applyData(measurement, studentPatientData.get());
                    }
                }
            }
            else if (content.getAnnuledMeasurementsTypeId() != null && !content.getAnnuledMeasurementsTypeId().isEmpty()) {
                //4.6 Аннулирование старых показателей антропометрических данных
                List<Integer> annuledMeasurements = content.getAnnuledMeasurementsTypeId().stream()
                        .map(Double::intValue).collect(Collectors.toList());

                int lastAnthropometryInfoSize = studentPatientData.get().getAnthropometryInfo().size();
                studentPatientData.get().getAnthropometryInfo()
                        .removeIf(anthropometryInfo -> annuledMeasurements.contains(anthropometryInfo.getMeasurementType()));

                if (lastAnthropometryInfoSize == studentPatientData.get().getAnthropometryInfo().size()) {
                    error = CustomErrorReason.REMOVING_RECORD_NOT_FOUND.format();
                }
            }
            if (studentPatientData.get().getAnthropometryInfo().isEmpty()) {
                studentPatientData.get().setAttachments(null);
            }
            studentPatientDataRepository.save(studentPatientData.get());

            return Optional.ofNullable(error);
        });
    }

    private void applyData(Measurement newData, StudentPatientData entity) {
        AnthropometryInfo newAnthropometryInfo = new AnthropometryInfo();
        newAnthropometryInfo.setMeasurementType(newData.getMeasurementType().intValue());
        newAnthropometryInfo.setMeasurementValue(newData.getMeasurementValue().floatValue());
        newAnthropometryInfo.setDocumentId(newData.getDocumentId());
        newAnthropometryInfo.setMeasurementDate(newData.getMeasurementDate());
        newAnthropometryInfo.setCentility(newData.getCentility());
        newAnthropometryInfo.setResultAssessmentId(newData.getResultAssessmentId() == null ? null : newData.getResultAssessmentId().longValue());
        entity.getAnthropometryInfo().add(newAnthropometryInfo);
    }

    @Override
    protected final PlannersEnum getPlanner() {
        return PlannersEnum.I_SCHR_8;
    }

    @Override
    protected TopicType getTopic() {
        return TopicType.LAST_ANTHROPOMETRY;
    }
}
