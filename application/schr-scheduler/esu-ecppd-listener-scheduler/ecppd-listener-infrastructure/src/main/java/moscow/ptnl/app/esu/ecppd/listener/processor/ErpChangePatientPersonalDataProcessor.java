package moscow.ptnl.app.esu.ecppd.listener.processor;

import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.esu.EsuConsumerProcessor;
import moscow.ptnl.app.esu.ecppd.listener.deserializer.PatientPersonalDataDeserializer;
import moscow.ptnl.app.esu.ecppd.listener.model.erp.PatientPersonalData;
import moscow.ptnl.app.esu.ecppd.listener.validator.ErpChangePatientBaseValidator;
import moscow.ptnl.app.model.TopicType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;

/**
 * I_SCHR_3 - erpChangePatientPersonalDataImportService - Получение сообщений из топика ErpChangePatientPersonalData
 *
 * @author sorlov
 */
@Component
public class ErpChangePatientPersonalDataProcessor extends EsuConsumerProcessor {

    @Autowired
    private ErpChangePatientBaseValidator erpChangePatientBaseValidator;

    @Autowired
    private PatientPersonalDataDeserializer patientPersonalDataDeserializer;

    @Override
    public TopicType getTopicType() {
        return TopicType.ERP_CHANGE_PATIENT_PERSONAL_DATA;
    }

    @Override
    protected Optional<String> validate(String message) {
        //4.1. Система проверяет, что сообщение соответствует формату JSON и возможно произвести его парсинг
        Optional<String> errorMsg = erpChangePatientBaseValidator.validate(message);

        if (errorMsg.isEmpty()) {
            HashSet<String> errorFields = new HashSet<>();
            //4.2. Проводится форматно-логический контроль полученных данных на наличие обязательных атрибутов
            PatientPersonalData content;

            try {
                content = patientPersonalDataDeserializer.apply(message);
            } catch (Exception ex) {
                return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(ex.getMessage()));
            }
            errorFields.add(content.getPatientId() == null || content.getPatientId() < 0 ? "emiasId" : null);
            errorFields.add(content.getUkl() == null ? "uklErp" : null);
            errorFields.add(content.getLastName() == null ? "lastName" : null);
            errorFields.add(content.getFirstName() == null ? "firstName" : null);
            errorFields.add(content.getPatronymic() == null ? "middleName" : null);
            errorFields.add(content.getGenderCode() == null ? "gender" : null);
            errorFields.add(content.getBirthDate() == null ? "birthDate" : null);
            errorFields.add(content.getUpdateDate() == null ? "operationDate" : null);

            errorFields.remove(null);

            if (!errorFields.isEmpty()) {
                return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(
                        "Пустое или некорректное значение полей: " + String.join(", ", errorFields)));
            }
        }
        return errorMsg;
    }
}
