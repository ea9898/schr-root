package moscow.ptnl.domain.entity.esu;

/**
 * Статус записи.
 * 
 * 0 - Новая запись
 * 1 - В работе
 * 2 - Обработано
 * 
 * @author sorlov
 */
public enum EsuStatusType {
    //Не менять местами, в базе храниться по порядковому номеру
    /** 0 - Новая запись. */
    NEW(0),
    /** 1 - В работе. */
    IN_PROGRESS(1), 
    /** 2 - Обработано. */
    PROCESSED(2);
    
    private final int status;
    
    EsuStatusType(int status) {
        this.status = status;
    }

    public Long getStatus() {
        return (long) status;
    }
    
}
