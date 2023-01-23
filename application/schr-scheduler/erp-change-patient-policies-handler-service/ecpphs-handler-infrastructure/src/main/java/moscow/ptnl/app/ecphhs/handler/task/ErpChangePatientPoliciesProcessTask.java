package moscow.ptnl.app.ecphhs.handler.task;

import moscow.ptnl.app.domain.model.es.Policy;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.ecppis.deserilizer.ErpChangePatientPoliciesDeserializer;
import moscow.ptnl.app.ecppis.model.Attribute;
import moscow.ptnl.app.ecppis.model.ErpChangePatientPolicies;
import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.infrastructure.repository.es.StudentPatientDataRepository;
import moscow.ptnl.app.model.PlannersEnum;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.task.BaseEsuProcessorTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * I_SCHR_4 - Обработка сообщений из топика ErpChangePatientPersonalData
 *
 * @author sorlov
 */
@Component
@PropertySource("classpath:esu.properties")
public class ErpChangePatientPoliciesProcessTask extends BaseEsuProcessorTask {

    @Autowired
    private ErpChangePatientPoliciesDeserializer erpChangePatientPoliciesDeserializer;

    @Autowired
    private StudentPatientDataRepository studentPatientDataRepository;

    @Override
    protected Optional<String> processMessage(String inputMsg) {

        //4.2 Система выполняет парсинг сообщения
        ErpChangePatientPolicies content;

        try {
            content = erpChangePatientPoliciesDeserializer.apply(inputMsg);
        } catch (Exception ex) {
            return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(ex.getMessage()));
        }
        return transactions.execute(s -> {
            //4.3 Поиск документа в индексе student_patient_registry_alias, где patientInfo.patientId = $.emiasId и _id = $.emiasId.
            Optional<StudentPatientData> studentPatientData = studentPatientDataRepository.findById(content.getEmiasId().toString());

            if (studentPatientData.isEmpty()
                    || studentPatientData.get().getPatientInfo() == null
                    || !Objects.equals(studentPatientData.get().getId(), String.valueOf(studentPatientData.get().getPatientInfo().getPatientId()))) {

                return Optional.of(CustomErrorReason.PATIENT_NOT_FOUND.format(content.getEmiasId()));
            }

            // 4.4 Для документа найденного на шаге 4.3, выполняется проверка условия
            Policy newPolicy = extractPolicy(content);

            if (!studentPatientData.get().getPolicy().getPolicyUpdateDate().isAfter(newPolicy.getPolicyUpdateDate())) {
                return Optional.of(CustomErrorReason.INFORMATION_IS_OUTDATED.format());
            } else {
                if (!newPolicy.getPolicyStatus().equals("N")) {
                    //4.5
                    StudentPatientData studentData = studentPatientData.get();
                    studentData.setPolicy(newPolicy);
                    studentPatientDataRepository.save(studentData);
                }

                // 4.6 Планировщик проставляет статус обработанному сообщению в ESU_INPUT
            }

            return Optional.empty();
        });
    }

    private Policy extractPolicy(ErpChangePatientPolicies content) {
        Optional<Attribute> policyChangeDateOpt = content.getEntityData().get(0).getAttributes().stream().filter(item -> item.getName().equals("policyChangeDate")).findFirst();
        Optional<Attribute> policyStatusOpt = content.getEntityData().get(0).getAttributes().stream().filter(item -> item.getName().equals("policyStatus")).findFirst();
        Optional<Attribute> policyNumberOpt = content.getEntityData().get(0).getAttributes().stream().filter(item -> item.getName().equals("policyNumber")).findFirst();

        Attribute policyNumber = policyNumberOpt.get();
        Attribute policyChangeDate = policyChangeDateOpt.get();
        Attribute policyStatus = policyStatusOpt.get();

        LocalDateTime policyChangeDateValue = LocalDateTime.parse(policyChangeDate.getValue().getValue());
        Policy policy = new Policy();

        policy.setPolicyNumber(policyNumber.getValue().getValue());
        policy.setPolicyUpdateDate(policyChangeDateValue);
        policy.setPolicyStatus(policyStatus.getValue().getCode());

        return policy;
    }

    @Override
    protected final PlannersEnum getPlanner() {
        return PlannersEnum.I_SCHR_6;
    }

    @Override
    protected TopicType getTopic() {
        return TopicType.ERP_CHANGE_PATIENT_POLICIES;
    }
}
