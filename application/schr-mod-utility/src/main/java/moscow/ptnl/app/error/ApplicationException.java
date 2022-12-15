package moscow.ptnl.app.error;

import ru.mos.emias.errors.domain.ErrorReason;

public class ApplicationException extends Exception {

    private final Validation validation;

    public ApplicationException(Validation validation) {
        this.validation = validation;
    }

    public ApplicationException(ErrorReason errorReason, ValidationParameter... parameters) {
        this(new Validation().error(errorReason, parameters));
    }

    public Validation getValidation() {
        return validation;
    }

    @Override
    public String getMessage() {
        if (!this.validation.getMessages().isEmpty()) {
            return this.validation.getMessages().get(0).getMessage();
        }
        return super.getMessage();
    }
}