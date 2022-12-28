package moscow.ptnl.app.erp.change.patient.policies.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moscow.ptnl.app.erp.change.patient.policies.validator.ErpChangePatientPoliciesValidator;
import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.esu.EsuConsumerProcessor;
import moscow.ptnl.app.model.TopicType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mos.emias.esu.model.Attribute;
import ru.mos.emias.esu.model.ErpChangePatientPolicies;

import java.util.HashSet;
import java.util.Optional;

/**
 * I_SCHR_5 - Получение сообщений из топика ErpChangePatientPolicies
 *
 * @author sevgeniy
 */
@Component
public class ECPPProcessor extends EsuConsumerProcessor {

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private ErpChangePatientPoliciesValidator erpChangePatientPoliciesValidator;

    @Override
    public TopicType getTopicType() {
        return TopicType.ERP_CHANGE_PATIENT_POLICIES;
    }

    @Override
    protected Optional<String> validate(String message) {
        //4.1. Система проверяет, что сообщение соответствует формату JSON и возможно произвести его парсинг
        Optional<String> errorMsg = erpChangePatientPoliciesValidator.validate(message);

        if (errorMsg.isEmpty()) {
            HashSet<String> errorFields = new HashSet<>();
            //4.2. Проводится форматно-логический контроль полученных данных на наличие обязательных атрибутов
            ErpChangePatientPolicies content;

            try {
                content = mapper.readValue(message, ErpChangePatientPolicies.class);
            } catch (JsonProcessingException ex) {
                return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(ex.getMessage()));
            }
            // patientId, $.emiasId
            if (content.getEmiasId() == null || content.getEmiasId() < 0) {
                errorFields.add("erpChangePatient.patientId");
            }

            // policyUpdateDate, $.entityData[0].attributes[?(@.name=="policyChangeDate")].value.value
            if (!(content.getEntityData() == null || content.getEntityData().isEmpty() || content.getEntityData().get(0).getAttributes() == null)) {
                Optional<Attribute> optionalAttribute = content.getEntityData().get(0).getAttributes()
                        .stream().filter(i -> i.getName().equals("policyUpdateDate")).findFirst();
                if (optionalAttribute.isEmpty() || optionalAttribute.get().getValue() == null ||
                        optionalAttribute.get().getValue().getValue() == null || optionalAttribute.get().getValue().getValue().strip().length() == 0) {
                    errorFields.add("erpChangePatient.policyUpdateDate");
                }
            }

            // policyUpdateDate, $.entityData[0].attributes[?(@.name=="policyChangeDate")].value.value
            if (!(content.getEntityData() == null || content.getEntityData().isEmpty() || content.getEntityData().get(0).getAttributes() == null)) {
                Optional<Attribute> optionalAttribute = content.getEntityData().get(0).getAttributes()
                        .stream().filter(i -> i.getName().equals("policyNumber")).findFirst();
                if (optionalAttribute.isEmpty() || optionalAttribute.get().getValue() == null
                        || optionalAttribute.get().getValue().getValue() == null || optionalAttribute.get().getValue().getValue().strip().length() == 0) {
                    errorFields.add("erpChangePatient.policyNumber");
                }
            }

            // policyStatus, $.entityData[0].attributes[?(@.name=="policyStatus")].value.code
            if (!(content.getEntityData() == null || content.getEntityData().isEmpty() || content.getEntityData().get(0).getAttributes() == null)) {
                Optional<Attribute> optionalAttribute = content.getEntityData().get(0).getAttributes()
                        .stream().filter(i -> i.getName().equals("policyStatus")).findFirst();
                if (optionalAttribute.isEmpty() || optionalAttribute.get().getValue() == null
                        || optionalAttribute.get().getValue().getCode() == null || optionalAttribute.get().getValue().getCode().strip().length() == 0) {
                    errorFields.add("erpChangePatient.policyStatus");
                }
            }

            errorFields.remove(null);

            if (!errorFields.isEmpty()) {
                return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(
                        "Пустое или некорректное значение полей: " + String.join(", ", errorFields)));
            }
        }
        return errorMsg;
    }
}
