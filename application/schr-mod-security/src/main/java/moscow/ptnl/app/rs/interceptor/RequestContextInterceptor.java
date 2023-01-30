package moscow.ptnl.app.rs.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import moscow.ptnl.app.rs.dto.UserContextObject;
import moscow.ptnl.app.rs.dto.UserContextRequest;
import moscow.ptnl.app.security.ExecutionContextHolder;
import moscow.ptnl.app.security.RequestContext;
import moscow.ptnl.app.security.SecurityExceptionTypes;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.phase.Phase;

import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Интерцептор REST-запросов, извлекающий информацию о пользователе
 * из тела запроса.
 * 
 * @author m.kachalov
 */
public class RequestContextInterceptor extends AbstractPhaseInterceptor<Message> {
    
    private final static Logger LOG = LoggerFactory.getLogger(RequestContextInterceptor.class);
    
    public RequestContextInterceptor() {
        super(Phase.PRE_INVOKE);
    }
    
    @Override
    public void handleMessage(Message message) throws Fault {  
        ExecutionContextHolder.setContext(
                new RequestContext(
                        getExchangeId(message),
                        getRequestUri(message),
                        getRequestedMethodName(message),
                        getRequestBody(message),
                        getUserContext(message)
                )
        );
    }
    
    private String getRequestUri(Message message) {
        Object obj = message.get("org.apache.cxf.request.uri");
        return obj != null ? obj.toString() : null;
    }
    
    private String getExchangeId(Message message) {
        Object obj = message.getExchange().get("exchangeId");
        return obj != null ? obj.toString() : null;
    }
    
    private String getRequestedMethodName(Message message) {
        return (String) message.getExchange().get("org.apache.cxf.resource.operation.name");
    }
    
    private String getRequestBody(Message message) {
        try {
            if (message.getExchange() != null && message.getExchange().getInMessage() != null) {
                try (CachedOutputStream contentIn = message.getExchange().getInMessage().getContent(CachedOutputStream.class)) {
                    return new String(contentIn.getBytes());
                }
            }
        } catch(Throwable th){
            LOG.error("Can't catch request message", th);
        }
        
        return null;
    }
    
    private moscow.ptnl.app.security.UserContext getUserContext(Message message) {
        //вытаскиваем UserContext из тела запроса
        MessageContentsList requestObjects = MessageContentsList.getContentsList(message);
        if (requestObjects == null || requestObjects.isEmpty()) {
            throw new moscow.ptnl.app.security.SecurityException(SecurityExceptionTypes.OTHER_SECURITY_EXCEPTION);
        }
        
        Object requestObject = requestObjects.get(0);
        if (requestObject == null || !(requestObject instanceof UserContextRequest)) {
            throw new moscow.ptnl.app.security.SecurityException(SecurityExceptionTypes.OTHER_SECURITY_EXCEPTION);
        }
        
        UserContextObject contextObject = ((UserContextRequest) requestObject).getUserContext();
        if (contextObject == null) {
            throw new moscow.ptnl.app.security.SecurityException(SecurityExceptionTypes.OTHER_SECURITY_EXCEPTION);
        }
        
        HttpServletRequest request = (HttpServletRequest) message.getExchange().getInMessage().get("HTTP.REQUEST");
        return new moscow.ptnl.app.security.UserContext(
                contextObject.getSystemName(),
                (contextObject.getUserName() != null) ? contextObject.getUserName() : "Не задано",
                contextObject.getUserRights(),
                contextObject.getJobExecutionId(),
                request.getRemoteAddr(),
                request.getRemoteHost()
        );
    }
}