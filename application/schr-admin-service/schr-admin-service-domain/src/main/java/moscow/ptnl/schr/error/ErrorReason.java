package moscow.ptnl.schr.error;

public enum ErrorReason {
    
    /** Код 200, выполнено "Учспешно". */
    SUCCESS(200, "Успешно"),
    /** Ошибка 400, "Некорректный запрос". */
    BAD_REQUEST(400, "Некорректный запрос"),
    /** Ошибка 401, "Не авторизован". */
    UNAUTHORIZED(401, "Не авторизован"),
    /** Ошибка 500, "Внутренняя ошибка сервера". */
    INTERNAL_SERVER_ERROR(500, "Внутренняя ошибка сервера");
    
    private final int code;
    private final String message;
    
    ErrorReason(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
    
}
