package moscow.ptnl.schr.transform;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import moscow.ptnl.schr.error.ServiceException;
import moscow.ptnl.schr.service.dto.ErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Обработчик ошибок REST-сервиса.
 * Логирует и пакует перехваченную ошибку в формат JSON.
 */
@Provider
public class RESTExceptionMapper implements ExceptionMapper<Throwable>{
    
    private final static Logger LOG = LoggerFactory.getLogger(RESTExceptionMapper.class);

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
        //вывод ошибки пользователю в формате JSON
        ErrorResponse error = new ErrorResponse(Integer.toString(statusCode.getStatusCode()), message);
        return Response
                .status(statusCode)
                .type(MediaType.APPLICATION_JSON)
                .encoding("utf-8")
                .entity(error)
                .build();
    }
    
}
