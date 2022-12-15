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
    ESU_UNREACHABLE("SCHR_100", "ЕСУ недоступна или вернула ошибку"),
    INCORRECT_FORMAT_ESU_MESSAGE("SCHR_101", "Некорректный формат сообщения ЕСУ: %s"),
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
