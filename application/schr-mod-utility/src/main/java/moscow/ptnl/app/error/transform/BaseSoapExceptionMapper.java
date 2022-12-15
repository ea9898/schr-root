package moscow.ptnl.app.error.transform;

import moscow.ptnl.app.error.ApplicationException;
import moscow.ptnl.app.error.Validation;
import moscow.ptnl.app.error.ValidationMessage;
import moscow.ptnl.app.error.ValidationParameter;
import ru.mos.emias.errors.domain.ErrorMessageType;
import ru.mos.emias.system.v1.faults.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BaseSoapExceptionMapper {

    public static <T> T map(Exception e, Class<T> faultType) {
        try {
            if (e instanceof ApplicationException) {
                BusinessFault fault = new BusinessFault();
                fault.setType(fault.getType());
                fault.setMessages(map(((ApplicationException) e).getValidation()));
                fault.setHasErrors(!((ApplicationException) e).getValidation().isSuccess());

                return faultType
                        .getDeclaredConstructor(String.class, BaseFault.class)
                        .newInstance(e.getMessage(), fault);
            }
            UnexpectedFault fault = new UnexpectedFault();
            fault.setType(fault.getType());
            //Todo сделать вывод версии
            fault.setVersion("");
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            fault.setStackTrace(map(stackTrace));

            return faultType
                        .getDeclaredConstructor(String.class, BaseFault.class, Throwable.class)
                        .newInstance((e.getMessage() != null ? e.getMessage() + " | " : "") + e.toString(), fault, e);
        } catch (Throwable err) {
            throw new IllegalStateException(err);
        }
    }

    public static UnexpectedFault.StackTrace map(StackTraceElement[] stackTrace) {
        UnexpectedFault.StackTrace trace = new UnexpectedFault.StackTrace();

        if (stackTrace != null) {
            trace.getStackTraceRecord().addAll(Arrays.stream(stackTrace)
                    .limit(10)
                    .map(BaseSoapExceptionMapper::map).collect(Collectors.toList()));
        }
        return trace;
    }

    public static UnexpectedFault.StackTrace.StackTraceRecord map(StackTraceElement stackTraceElement) {
        UnexpectedFault.StackTrace.StackTraceRecord record = new UnexpectedFault.StackTrace.StackTraceRecord();
        record.setDeclaringClass(stackTraceElement.getClassName());
        record.setFileName(stackTraceElement.getFileName());
        record.setLineNumber(stackTraceElement.getLineNumber());
        record.setMethodName(stackTraceElement.getMethodName());

        return record;
    }

    public static ErrorMessageCollection map(Validation validation) {
        ErrorMessageCollection messageCollection = new ErrorMessageCollection();

        if (validation.getMessages() != null && !validation.getMessages().isEmpty()) {
            messageCollection.getMessage().addAll(validation.getMessages().stream().map(BaseSoapExceptionMapper::map).collect(Collectors.toList()));
        }
        return messageCollection;
    }

    public static ErrorMessage map(ValidationMessage validationMessage) {
        if (validationMessage != null) {
            ErrorMessage message = new ErrorMessage();
            message.setCode(validationMessage.getCode());
            message.setMessage(validationMessage.getMessage());
            message.setType(map(validationMessage.getType()));
            message.setParameters(map(validationMessage.getParameters()));

            return message;
        }
        return null;
    }

    public static ErrorMessageTypes map(ErrorMessageType messageType) {
        switch (messageType) {
            case INFO: return ErrorMessageTypes.INFO;
            case WARNING: return ErrorMessageTypes.WARNING;
            case ERROR:
            default:
                return ErrorMessageTypes.ERROR;
        }
    }

    public static ErrorMessage.Parameters map(List<ValidationParameter> parameters) {
        if (parameters != null && !parameters.isEmpty()) {
            ErrorMessage.Parameters newParameters = new ErrorMessage.Parameters();
            newParameters.getParameter().addAll(parameters.stream().map(BaseSoapExceptionMapper::map).collect(Collectors.toList()));

            return newParameters;
        }
        return null;
    }

    public static KeyValuePair map(ValidationParameter parameter) {
        if (parameter != null) {
            KeyValuePair pair = new KeyValuePair();
            pair.setKey(parameter.getCode());
            pair.setValue(parameter.getValue());

            return pair;
        }
        return null;
    }
}

