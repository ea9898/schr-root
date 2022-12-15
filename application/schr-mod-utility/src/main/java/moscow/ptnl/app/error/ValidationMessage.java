package moscow.ptnl.app.error;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import ru.mos.emias.errors.domain.ErrorMessageType;
import ru.mos.emias.errors.domain.ErrorReason;
import ru.mos.emias.errors.domain.Message;

/**
 * Результат валидации.
 * 
 */
public class ValidationMessage extends Message {
    
    /**
     * Тип сообщения
     */
    private ErrorMessageType type;

    /**
     * Параметры валидации
     */
    private final List<ValidationParameter> parameters = new ArrayList<>();

    public ValidationMessage() {
    }

    public ValidationMessage(ErrorReason errorReason, ErrorMessageType type) {
        if (errorReason == null) {
            throw new IllegalArgumentException("errorReason не может быть null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type не может быть null");
        }
        setCode(errorReason.getCode());
        setMessage(errorReason.getDescription());
        this.type = type;
    }

    public ValidationMessage addParameter(ValidationParameter... params) {
        if (params == null)
            return this;
        Collections.addAll(parameters, params);
        return this;
    }

    public ErrorMessageType getType() {
        return type;
    }

    public void setType(ErrorMessageType type) {
        this.type = type;
    }

    public List<ValidationParameter> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidationMessage that = (ValidationMessage) o;

        if (getCode() != null ? !getCode().equals(that.getCode()) : that.getCode() != null) return false;
        if (getMessage() != null ? !getMessage().equals(that.getMessage()) : that.getMessage() != null) return false;
        if (type != that.type) return false;
        return !(parameters != null ? !parameters.equals(that.parameters) : that.parameters != null);

    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getCode());
        result = 31 * result + Objects.hash(getMessage());
        result = 31 * result + Objects.hash(type);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s  %s \n", getCode(), getMessage());
    }
}