package moscow.ptnl.app.security;

/**
 * Инкапсулирует данные о запросе.
 *
 * @author m.kachalov
 */
public class RequestContext extends moscow.ptnl.app.security.ExecutionContext {
    
    private final String methodName; //имя вызываемого метода web/rest-сервиса
    private final UserContext userContext;
    private final String uri; //полный uri запроса
    private final String requestBody;
    
    public RequestContext(String uri, String methodName, String requestBody, UserContext userContext) {
        this(null, uri, methodName, requestBody, userContext);
    }
    
    /**
     * 
     * @param exchangeId идентификатор SOAP-запроса/ответа используемый в логировании Apache CXF 
     * @param uri 
     * @param methodName
     * @param userContext 
     * @param requestBody 
     */
    public RequestContext(
            String exchangeId, 
            String uri, 
            String methodName,
            String requestBody, 
            UserContext userContext
    ) {
        super(exchangeId);
        this.uri = uri;
        this.methodName = methodName;
        this.requestBody = requestBody;
        this.userContext = userContext;
    }
    
    //пакетный доступ, чтобы зря не дергали метод
    String getRequestId() {
        return this.getExecutionId();
    }

    //пакетный доступ, чтобы зря не дергали метод
    String getMethodName() {
        return methodName;
    }
    
    //пакетный доступ, чтобы зря не дергали метод
    UserContext getUserContext() {
        return this.userContext;
    }

    String getUri() {
        return uri;
    }

    String getRequestBody() {
        return requestBody;
    }
    
}
