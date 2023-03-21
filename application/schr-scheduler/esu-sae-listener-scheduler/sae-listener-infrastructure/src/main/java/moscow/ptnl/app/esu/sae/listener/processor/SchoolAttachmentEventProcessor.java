package moscow.ptnl.app.esu.sae.listener.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.esu.EsuConsumerProcessor;
import moscow.ptnl.app.esu.sae.listener.deserializer.PatientSchoolAttachmentDeserializer;
import moscow.ptnl.app.esu.sae.listener.model.erp.PatientSchoolAttachment;
import moscow.ptnl.app.esu.sae.listener.validator.ErpChangePatientSchoolBaseValidator;
import moscow.ptnl.app.model.TopicType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mos.emias.esu.model.ErpChangePatientSchoolBase;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

/**
 * I_SCHR_11 - schoolAttachmentEventImportService - Получение сообщений из топика SchoolAttachmentEvent
 *
 * @author sorlov
 */
@Component
public class SchoolAttachmentEventProcessor extends EsuConsumerProcessor {

    @Autowired
    private ErpChangePatientSchoolBaseValidator erpChangePatientSchoolBaseValidator;

    @Autowired
    private PatientSchoolAttachmentDeserializer patientSchoolAttachmentDeserializer;

    @Override
    public TopicType getTopicType() {
        return TopicType.SCHOOL_ATTACHMENT_EVENT;
    }

    @Autowired
    protected ObjectMapper mapper;

    @Override
    public Optional<String> validate(String message) {
        //4.1. Система проверяет, что сообщение соответствует формату JSON и возможно произвести его парсинг
        Optional<String> errorMsg = erpChangePatientSchoolBaseValidator.validate(message);

        if (errorMsg.isEmpty()) {
            HashSet<String> errorFields = new HashSet<>();
            //4.2. Проводится форматно-логический контроль полученных данных на наличие обязательных атрибутов
            PatientSchoolAttachment content;

            try {
                content = patientSchoolAttachmentDeserializer.apply(message);
            } catch (Exception ex) {
                return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(ex.getMessage()));
            }
            errorFields.add(content.getPatientId() == null || content.getPatientId() < 0 ? "emiasId" : null);
            errorFields.add(content.getStudentId() == null ? "studentId" : null);
//            errorFields.add(content.getStudentPersonId() == null ? "studentPersonId" : null);
            errorFields.add(content.getUpdateDate() == null ? "operationDate" : null);

            errorFields.add(!content.getAttachmentIdChecked() ? "attachId" : null);
//            errorFields.add(!content.getOrganizationIdChecked() ? "organizationId" : null);
            errorFields.add(!content.getAreaIdChecked() ? "areaId" : null);
            errorFields.add(!content.getAttachStartDateChecked() ? "attachStartDate" : null);
//            errorFields.add(!content.getClassUidChecked() ? "classUid" : null);
            errorFields.add(!content.getEducationFormChecked() ? "educationForm.id" : null);
            errorFields.add(!content.getEducationFormNameChecked() ? "educationForm" : null);
//            errorFields.add(!content.getAcademicYearIdChecked() ? "academicYear.id" : null);
//            errorFields.add(!content.getAcademicYearChecked() ? "academicYear" : null);
            errorFields.add(!content.getActual() ? "isActual" : null);

            errorFields.remove(null);

            if (!errorFields.isEmpty()) {
                return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(
                        "Пустое или некорректное значение полей: " + String.join(", ", errorFields)));
            }
        }
        return errorMsg;
    }
}
