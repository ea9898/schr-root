package moscow.ptnl.app.ecppd.handler.task;

import moscow.ptnl.app.domain.model.es.StudentAttachInfo;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.error.CustomErrorReason;
import moscow.ptnl.app.esu.ecppd.listener.deserializer.SchoolAttachmentEventDeserializer;
import moscow.ptnl.app.esu.ecppd.listener.model.erp.AttachmentData;
import moscow.ptnl.app.esu.ecppd.listener.model.erp.SchoolAttachmentData;
import moscow.ptnl.app.infrastructure.repository.es.StudentPatientDataRepository;
import moscow.ptnl.app.model.PlannersEnum;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.task.BaseEsuProcessorTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * I_SCHR_12 - Обработка сообщений топика SchoolAttachmentEvent
 */

@Component
@PropertySource("classpath:esu.properties")
public class SchoolAttachmentEventProcessTask extends BaseEsuProcessorTask {

    @Autowired
    private SchoolAttachmentEventDeserializer schoolAttachmentEventDeserializer;

    @Autowired
    private StudentPatientDataRepository studentPatientDataRepository;


    @Override
    protected Optional<String> processMessage(String inputMsg) {
        // 4.2 Система выполняет парсинг сообщения
        SchoolAttachmentData content;
        try {
            content = schoolAttachmentEventDeserializer.apply(inputMsg);
        } catch (Exception ex) {
            return Optional.of(CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(ex.getMessage()));
        }

        return transactions.execute(s -> {
            // 4.3 Поиск документа в индексе student_patient_registry
            Optional<StudentPatientData> studentPatientDataOptional = studentPatientDataRepository.findById(content.getPatientId().toString());

            StudentPatientData studentPatientData;

            if (studentPatientDataOptional.isEmpty()) {
                studentPatientData = new StudentPatientData();
                try {
                    // TODO алгоритм A_SCHR_1 - пункты 1, 2
                } catch (Exception e) {
                    return Optional.of(CustomErrorReason.CREATE_NEW_PATIENT_EXCEPTION.format("КОД", e.getMessage())); // TODO Что за код???
                }

                // TODO алгоритм A_SCHR_1 - пункты 3, 4
                // TODO алгоритм A_SCHR_1 - пункты 5, 6
                // TODO алгоритм A_SCHR_1 - пункт 7

            } else {
                studentPatientData = studentPatientDataOptional.get();
            }

            // 4.4 Система для каждого значения из массива $.entityData[*]
            for (AttachmentData attachmentData : content.getAttachmentDataList()) {
                // блок/блоки - имеется в виду, что должен быть 1 блок, но если их больше одного, то это всё дубли
                List<StudentAttachInfo> studInfoList = studentPatientData.getStudInfo().stream()
                        .filter(info -> info.getAttachId().equals(attachmentData.getAttachId()))
                        .collect(Collectors.toList());
                Long ageMax = settingService.getSettingProperty(getPlanner().getPlannerName() + ".ageMax", Long.class, false);
                if (!studInfoList.isEmpty()) {
                    if (studInfoList.get(0).getStudChangeDate().isBefore(content.getStudChangeDate())) {
                        if (attachmentData.getActual()) {
                            // Система удаляет найденный блок (блоки) и переходит на следующий шаг (4.4.2 Система записывает блок в элемент индекса studInfo)
                            studentPatientData.getStudInfo().removeAll(studInfoList);
                            applyData(content, attachmentData, studentPatientData);
                        } else {
                            //Система удаляет найденный блок/блоки
                            studentPatientData.getStudInfo().removeAll(studInfoList);
                            //ЕСЛИ после удаления блока/блоков в элементе индекса studInfo не останется блоков в массиве
                            if (studentPatientData.getStudInfo().isEmpty()) {
                                // Система проверяет возраст пациента
                                if ((LocalDate.now().getYear() - studentPatientData.getPatientInfo().getBirthDate().getYear()) >= ageMax) {
                                    // Система удаляет документ с пациентом _id = $.emiasId и переходит на шаг 4.5
                                    studentPatientDataRepository.delete(studentPatientData);
                                    return Optional.empty();
                                } else {
                                    // Система удаляет элемент индекса studInfo и переходит на шаг 4.5
                                    studentPatientData.setStudInfo(null);
                                    studentPatientDataRepository.save(studentPatientData);
                                    return Optional.empty();
                                }
                            } else {
                                // переходит на шаг 4.5
                                return Optional.empty();
                            }
                        }
                    } else {
                        // Переход на шаг 4.5 с ошибкой SCHR_107
                        return Optional.of(CustomErrorReason.INFORMATION_IS_OUTDATED.format());
                    }
                } else {
                    if (attachmentData.getActual()) {
                        // Система переходит на следующий шаг (4.4.2 Система записывает блок в элемент индекса studInfo)
                        applyData(content, attachmentData, studentPatientData);
                    } else {
                        // Система проверяет возраст пациента
                        if ((LocalDate.now().getYear() - studentPatientData.getPatientInfo().getBirthDate().getYear()) >= ageMax) {
                            // Система удаляет документ с пациентом _id = $.emiasId и переходит на шаг 4.5 с ошибкой SCHR_106
                            studentPatientDataRepository.delete(studentPatientData);
                            return Optional.of(CustomErrorReason.RECORD_NOT_FOUND.format());
                        } else {
                            // Система переходит на шаг 4.5 с ошибкой SCHR_106
                            return Optional.of(CustomErrorReason.RECORD_NOT_FOUND.format());
                        }
                    }
                }
            }
            return Optional.empty();
        });
    }

    private void applyData(SchoolAttachmentData newSchoolAttachData, AttachmentData newAttachData, StudentPatientData entity) {
        StudentAttachInfo studentAttachInfo = new StudentAttachInfo();
        studentAttachInfo.setAttachId(newAttachData.getAttachId());
        studentAttachInfo.setOrganizationId(newAttachData.getOrganizationId());
//        studentAttachInfo.setStudentIdKis(newSchoolAttachData.getStudentIdKis());
//        studentAttachInfo.setStudentIdMesh(newSchoolAttachData.getStudentIdMesh());
//        studentAttachInfo.setClassIdMesh(newAttachData.getClassIdMesh());
//        studentAttachInfo.setAcademicYearId(newAttachData.getAcademicYearId());
//        studentAttachInfo.setAcademicYearName(newAttachData.getAcademicYearName());
//        studentAttachInfo.setStudChangeDate(newSchoolAttachData.getStudChangeDate());

        entity.getStudInfo().add(studentAttachInfo);
        studentPatientDataRepository.save(entity);
    }

    @Override
    protected PlannersEnum getPlanner() {
        return PlannersEnum.I_SCHR_12;
    }

    @Override
    protected TopicType getTopic() {
        return TopicType.SCHOOL_ATTACHMENT_EVENT;
    }

}
