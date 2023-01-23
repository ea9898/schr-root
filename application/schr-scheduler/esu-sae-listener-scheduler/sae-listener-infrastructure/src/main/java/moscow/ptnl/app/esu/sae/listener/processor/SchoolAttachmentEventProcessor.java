package moscow.ptnl.app.esu.sae.listener.processor;

import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.esu.EsuConsumerProcessor;
import moscow.ptnl.app.esu.sae.listener.deserializer.PatientSchoolAttachmentDeserializer;
import moscow.ptnl.app.esu.sae.listener.model.erp.PatientSchoolAttachment;
import moscow.ptnl.app.esu.sae.listener.validator.ErpChangePatientSchoolBaseValidator;
import moscow.ptnl.app.model.TopicType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
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

    @Override
    protected Optional<String> validate(String message) {
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
            errorFields.add(content.getAttachmentId() == null ? "attachId" : null);
            errorFields.add(content.getOrganizationId() == null ? "organizationId" : null);
            errorFields.add(content.getStudentId() == null ? "studentId" : null);
            errorFields.add(content.getStudentPersonId() == null ? "studentPersonId" : null);
            errorFields.add(content.getAcademicYearId() == null ? "academicYear.id" : null);
            errorFields.add(content.getAcademicYear() == null ? "academicYear" : null);
            errorFields.add(content.getClassUid() == null ? "classUid" : null);
            errorFields.add(content.getActual() == null ? "isActual" : null);
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