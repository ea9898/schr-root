package moscow.ptnl.app.esu.ecppd.listener.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import moscow.ptnl.app.domain.model.es.Gender;
import moscow.ptnl.app.domain.model.es.PatientInfo;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.esu.ecppd.listener.deserializer.PatientPersonalDataDeserializer;
import moscow.ptnl.app.esu.ecppd.listener.model.erp.PatientPersonalData;
import moscow.ptnl.app.infrastructure.repository.es.StudentPatientDataRepository;
import moscow.ptnl.app.model.PlannersEnum;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.task.BaseEsuProcessorTask;
import moscow.ptnl.app.util.service.BusinessUtil;

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
public class ErpChangePatientPersonalDataProcessTask extends BaseEsuProcessorTask {

    @Autowired
    private PatientPersonalDataDeserializer patientPersonalDataDeserializer;

    @Autowired
    private StudentPatientDataRepository studentPatientDataRepository;

    @Override
    protected Optional<String> processMessage(String inputMsg) {
        //4.2 Система выполняет парсинг сообщения
        PatientPersonalData content;

        try {
            content = patientPersonalDataDeserializer.apply(inputMsg);
        } catch (Exception ex) {
            return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(ex.getMessage()));
        }
        return transactions.execute(s -> {
            //4.3 Поиск документа в индексе student_patient_registry
            Optional<StudentPatientData> studentPatientData = studentPatientDataRepository.findById(content.getPatientId().toString());

            if (studentPatientData.isEmpty()
                    || studentPatientData.get().getPatientInfo() == null
                    || !Objects.equals(studentPatientData.get().getId(), String.valueOf(studentPatientData.get().getPatientInfo().getPatientId()))) {
                return Optional.of(CustomErrorReason.PATIENT_NOT_FOUND.format(content.getPatientId()));
            }
            //4.4 Для документа найденного на шаге 4.3, выполняется проверка условия
            if (studentPatientData.get().getPatientInfo() == null
                    || !content.getUpdateDate().isAfter(studentPatientData.get().getPatientInfo().getUpdateDate())) {
                return Optional.of(CustomErrorReason.INFORMATION_IS_OUTDATED.format());
            }
            //4.5 Система добавляет элемент индекса patientInfo в документ
            applyData(content, studentPatientData.get());

            return Optional.empty();
        });
    }

    private void applyData(PatientPersonalData newData, StudentPatientData entity) {
        PatientInfo patientInfo = new PatientInfo();
        patientInfo.setPatientId(newData.getPatientId());
        patientInfo.setUkl(newData.getUkl());
        patientInfo.setLastName(newData.getLastName());
        patientInfo.setFirstName(newData.getFirstName());
        patientInfo.setPatronymic(newData.getPatronymic());
        patientInfo.setFullName(BusinessUtil.convertFullNameToString(newData.getFirstName(), newData.getLastName(), newData.getPatronymic()));
        patientInfo.setBirthDate(newData.getBirthDate());
        patientInfo.setGenderCode(newData.getGenderCode() == null ? null : Long.valueOf(newData.getGenderCode()));
        patientInfo.setDeathDate(newData.getDeathDate());
        patientInfo.setUpdateDate(newData.getUpdateDate());
        entity.setPatientInfo(patientInfo);
        studentPatientDataRepository.save(entity);
    }

    @Override
    protected final PlannersEnum getPlanner() {
        return PlannersEnum.I_SCHR_4;
    }

    @Override
    protected TopicType getTopic() {
        return TopicType.ERP_CHANGE_PATIENT_PERSONAL_DATA;
    }
}
