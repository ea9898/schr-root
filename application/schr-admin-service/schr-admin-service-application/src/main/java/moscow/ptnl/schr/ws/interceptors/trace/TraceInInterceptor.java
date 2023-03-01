package moscow.ptnl.schr.ws.interceptors.trace;

import org.apache.cxf.phase.Phase;
import org.springframework.stereotype.Component;

@Component
public class TraceInInterceptor extends TraceInterceptor {

    public TraceInInterceptor() {
        super(Phase.UNMARSHAL);
    }
}
