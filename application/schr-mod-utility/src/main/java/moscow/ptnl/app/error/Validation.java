package moscow.ptnl.app.error;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import ru.mos.emias.errors.domain.ErrorMessageType;
import ru.mos.emias.errors.domain.ErrorReason;

/**
 * Результат выполнения валидации.
 * 
 */
public class Validation {

    /**
     * Результат проверки
     */
    private boolean success;
    /**
     * Перечень ошибок
     */
    private List<ValidationMessage> messages = new ArrayList<>();

    public Validation() { success = true; }

    /**
     * Добавить сообщение
     * @param message
     */
    private void addMessage(ValidationMessage message){
        if (message == null) {
            throw new IllegalArgumentException("message can't be null");
        }        
        messages.add(message);

        List<String> params = new ArrayList<>();
        if (message.getParameters() != null)
            params.addAll(message.getParameters().stream().map(c -> c.getValue()).collect(Collectors.toList()));

        if (params.size() > 0)
            message.setMessage(String.format(message.getMessage(), params.toArray()));
        success = success && message.getType() != ErrorMessageType.ERROR;
    }

    public Validation merge(Validation b){
        b.messages.forEach(this::addMessage);
        return this;
    }

    public static Validation merge(Validation a, Validation b){
        Validation result = new Validation();
        a.messages.forEach(result::addMessage);
        b.messages.forEach(result::addMessage);
        return result;
    }

    public Validation error(ErrorReason reason, ValidationParameter... parameters){
        addMessage(new ValidationMessage(reason, ErrorMessageType.ERROR).addParameter(parameters));
        return this;
    }

    public Validation warning(ErrorReason reason, ValidationParameter... parameters){
        addMessage(new ValidationMessage(reason, ErrorMessageType.WARNING).addParameter(parameters));
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ValidationMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ValidationMessage> messages) {
        this.messages = messages;
    }

    public void reset() {
        messages.clear();
        success = true;
    }
}