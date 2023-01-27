package moscow.ptnl.app.security;

public class ExecutionContextHolder {

    private static final ThreadLocal<moscow.ptnl.app.security.ExecutionContext> EXECUTION_CONTEXT = new ThreadLocal<>();

    @SuppressWarnings("unchecked")
    public static <T extends moscow.ptnl.app.security.ExecutionContext> T getContext() {
        return (T) EXECUTION_CONTEXT.get();
    }
    
    public static String getExecutionId() {
        moscow.ptnl.app.security.ExecutionContext context = getContext();
        if (context == null) {
            return null;
        }
        return context.getExecutionId();
    }
    
    public static String getMethodName() {
        moscow.ptnl.app.security.ExecutionContext context = getContext();
        if (context != null && context instanceof RequestContext) {
            return ((RequestContext) context).getMethodName();
        }
        return null;
    }
    
    public static moscow.ptnl.app.security.UserContext getUserContext() {
        moscow.ptnl.app.security.ExecutionContext context = getContext();
        if (context != null && context instanceof RequestContext) {
            return ((RequestContext) context).getUserContext();
        }
        return null;
    }
    
    public static String getUri() {
        moscow.ptnl.app.security.ExecutionContext context = getContext();
        if (context != null && context instanceof RequestContext) {
            return ((RequestContext) context).getUri();
        }
        return null;
    }
    
    public static String getUserName() {
        UserContext context = getUserContext();
        return context != null ? context.getUserName() : null;
    }
    
    public static String getSystemName() {
        UserContext context = getUserContext();
        return context != null ? context.getSystemName() : null;
    }

    public static String getRequestBody() {
        moscow.ptnl.app.security.ExecutionContext context = getContext();
        if (context != null && context instanceof RequestContext) {
            return ((RequestContext) context).getRequestBody();
        }
        return null;
    } 
    
    public static void setContext(moscow.ptnl.app.security.ExecutionContext context) {
        if (context != null) {
            EXECUTION_CONTEXT.set(context);
        } else {
            EXECUTION_CONTEXT.remove();
        }
    }
}
