package moscow.ptnl.schr.ws.interceptors.trace;

import org.apache.cxf.phase.Phase;
import org.springframework.stereotype.Component;

@Component
public class TraceOutInterceptor extends TraceInterceptor {

    public TraceOutInterceptor() {
        super(Phase.POST_MARSHAL);
    }
}
