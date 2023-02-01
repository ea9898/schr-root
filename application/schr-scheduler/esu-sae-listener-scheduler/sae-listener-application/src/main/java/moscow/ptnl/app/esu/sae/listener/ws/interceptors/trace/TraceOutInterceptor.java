package moscow.ptnl.app.esu.sae.listener.ws.interceptors.trace;

import org.apache.cxf.phase.Phase;
import org.springframework.stereotype.Component;

@Component
class TraceOutInterceptor extends TraceInterceptor {

    TraceOutInterceptor() {
        super(Phase.POST_MARSHAL);
    }
}
