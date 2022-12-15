package moscow.ptnl.app.error;

/**
 *
 * @author m.kachalov
 */
public class EmiasRuntimeException extends RuntimeException {

    private final CustomErrorReason errorReason;

    public EmiasRuntimeException(CustomErrorReason errorReason){
        super(errorReason.format());
        this.errorReason = errorReason;
    }

    public EmiasRuntimeException(CustomErrorReason errorReason, String message){
        super(message);
        this.errorReason = errorReason;
    }

    public EmiasRuntimeException(CustomErrorReason errorReason, String message, Throwable ex) {
        super(message, ex);
        this.errorReason = errorReason;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return errorReason == null;
    }

    /**
     * @return the error
     */
    public CustomErrorReason getErrorReason() {
        return errorReason;
    }
}
