package moscow.ptnl.schr.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.UUID;

import moscow.ptnl.app.security.ExecutionContextHolder;
import moscow.ptnl.schr.algorithm.JournalRequestHelper;
import moscow.ptnl.schr.error.ServiceException;
import moscow.ptnl.schr.service.dto.ErrorResponse;
import org.apache.tomcat.util.json.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Обработчик ошибок REST-сервиса.
 * Логирует и пакует перехваченную ошибку в формат JSON.
 *
 * @author m.kachalov
 */
@Provider
public class RESTExceptionMapper implements ExceptionMapper<Throwable> {

    private final static Logger LOG = LoggerFactory.getLogger(RESTExceptionMapper.class);

    private final JournalRequestHelper journalRequestHelper;

    public RESTExceptionMapper(JournalRequestHelper journalRequestHelper) {
        this.journalRequestHelper = journalRequestHelper;
    }

    @Override
    public Response toResponse(Throwable e) {
        Response.Status statusCode = Response.Status.INTERNAL_SERVER_ERROR;
        String message = e.getMessage();

        if (e instanceof ServiceException) {
            ServiceException se = (ServiceException) e;
            message = se.getMessage();
            switch (se.getErrorReason()) {
                case BAD_REQUEST:
                    statusCode = Response.Status.BAD_REQUEST;
                    break;
                case UNAUTHORIZED:
                    statusCode = Response.Status.UNAUTHORIZED;
                    break;
                case INTERNAL_SERVER_ERROR:
                    statusCode = Response.Status.INTERNAL_SERVER_ERROR;
                    break;
            }
        }

        //АС.1 Сохранение запроса
        UUID id = journalRequestHelper.addJournalRequestRecord(
                ExecutionContextHolder.getUserName(),
                ExecutionContextHolder.getSystemName(),
                ExecutionContextHolder.getUri(),
                ExecutionContextHolder.getRequestBody(),
                statusCode.getStatusCode(),
                message
        );

        //вывод ошибки пользователю в формате JSON
        Object jsonObj = null;
        try {
            jsonObj = new ObjectMapper().readValue(message, Object.class);
        } catch (JsonProcessingException ex) {
            jsonObj = message;
        }
        ErrorResponse error = null;
        error = new ErrorResponse(
                Integer.toString(statusCode.getStatusCode()),
                jsonObj,
                id.toString()
        );

        Response response = Response
                .status(statusCode)
                .type(MediaType.APPLICATION_JSON)
                .encoding("utf-8")
                .entity(error)
                .build();
        return response;
    }

}
