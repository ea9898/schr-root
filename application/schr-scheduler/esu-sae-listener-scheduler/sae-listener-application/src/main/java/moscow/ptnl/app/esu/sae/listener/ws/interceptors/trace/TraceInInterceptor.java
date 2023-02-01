package moscow.ptnl.app.esu.sae.listener.ws.interceptors.trace;

import org.apache.cxf.phase.Phase;
import org.springframework.stereotype.Component;

@Component
public class TraceInInterceptor extends TraceInterceptor {

    TraceInInterceptor() {
        super(Phase.UNMARSHAL);
    }
}
