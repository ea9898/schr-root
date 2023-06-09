package moscow.ptnl.app.error;

import ru.mos.emias.errors.domain.ErrorMessageType;
import ru.mos.emias.errors.domain.ErrorReason;

/**
 * Пользовательское сообщение.
 * 
 * @author m.kachalov
 */
public enum CustomErrorReason implements ErrorReason {

    UNEXPECTED_ERROR("SCHR_000", "Непредвиденная ошибка. %s"),
    ESU_UNREACHABLE("SCHR_100", "ЕСУ недоступна или вернула ошибку: %s"),
    INCORRECT_FORMAT_ESU_MESSAGE("SCHR_101", "Некорректный формат сообщения ЕСУ: %s"),
    INCORRECT_ATTACHMENT_TYPE("SCHR_102", "Полученное прикрепление не является педиатрическим или терапевтическим"),
    PATIENT_NOT_FOUND("SCHR_104", "Пациент с идентификатором %s не найден в системе"),
    REMOVING_RECORD_NOT_FOUND("SCHR_106", "Запись, которую необходимо удалить, не найдена"),
    INFORMATION_IS_OUTDATED("SCHR_107", "Получена более старая информация, чем содержится в индексе"),
    CREATE_NEW_PATIENT_EXCEPTION("SCHR_108", "Ошибка при создании нового пациента %s"),
    SCHR_201("SCHR_201", "%s"),
    SCHR_202("SCHR_202", "%s"),
    SCHR_203("SCHR_203", "%s"),
    SCHR_204("SCHR_204", "%s"),
    ;

    private final String description;
    private final String code;
    private final ErrorMessageType messageType;
    
    CustomErrorReason(String code, String description) {
        this(description, code, ErrorMessageType.ERROR);
    }
    
    CustomErrorReason(String description, String code, ErrorMessageType messageType) {
        this.description = description;
        this.code = code;
        this.messageType = messageType;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public ErrorMessageType getMessageType() {
        return this.messageType;
    }

    public String format(Object ... objects) {
        return getCode() + " - " + String.format(getDescription(), objects);
    }

    public String format(Throwable th) {
        return getCode() + " - " + String.format(getDescription(), th.getClass().getSimpleName() + ":" + th.getMessage());
    }

}
