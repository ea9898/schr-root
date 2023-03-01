package moscow.ptnl.schr.error;

/**
 *
 * @author m.kachalov
 */
public class ServiceException extends Exception {
    
    private final ErrorReason errorReason;    
    
    public ServiceException(ErrorReason errorReason) {
        super(errorReason.getMessage());
        this.errorReason = errorReason;
    }
    
    public ServiceException(ErrorReason errorReason, String message) {
        super(message);
        this.errorReason = errorReason;
    }

    public ErrorReason getErrorReason() {
        return errorReason;
    }
    
}
