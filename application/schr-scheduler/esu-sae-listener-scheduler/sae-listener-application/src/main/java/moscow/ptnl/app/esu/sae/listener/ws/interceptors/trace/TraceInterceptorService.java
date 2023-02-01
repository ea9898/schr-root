package moscow.ptnl.app.esu.sae.listener.ws.interceptors.trace;

import org.apache.cxf.interceptor.InterceptorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TraceInterceptorService {

    @Autowired
    private TraceOutInterceptor outInterceptor;

    @Autowired
    private TraceInInterceptor inInterceptor;

    public void setupInterceptors(InterceptorProvider endpoint) {
        endpoint.getInInterceptors().add(inInterceptor);
        endpoint.getInFaultInterceptors().add(inInterceptor);
        endpoint.getOutInterceptors().add(outInterceptor);
        endpoint.getOutFaultInterceptors().add(outInterceptor);
    }
}
