package moscow.ptnl.schr.ws.interceptors.trace;


import moscow.ptnl.app.trace.LoggerDataHolder;
import moscow.ptnl.app.trace.TreeCallStackLogger;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.service.model.OperationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

abstract class TraceInterceptor extends AbstractPhaseInterceptor<Message> {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    public TraceInterceptor(String phase) {
        super(phase);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        TreeCallStackLogger callStackLogger = LoggerDataHolder.getCurrentRequest();

        if (MessageUtils.isRequestor(message) ^ MessageUtils.isOutbound(message)) {
            if (callStackLogger != null) {
                try {
                    callStackLogger.afterCall(message.getContent(Exception.class) != null);
                } finally {
                    if (callStackLogger.currentIsRoot()) {
                        LoggerDataHolder.endRequest();
                    }
                }
            }
        }
        else if (message.getExchange() != null) {
            StringBuilder name = new StringBuilder();
            OperationInfo opInfo = message.getExchange().get(OperationInfo.class);
            name.append(message.getExchange().getService().getName().getLocalPart())
                    .append("/")
                    .append(opInfo == null ? "[Null]" : opInfo.getName().getLocalPart());

            if (callStackLogger == null) {
                callStackLogger = LoggerDataHolder.startRequest();
            }
            callStackLogger.beforeCall(name.toString());
        }
    }
}
