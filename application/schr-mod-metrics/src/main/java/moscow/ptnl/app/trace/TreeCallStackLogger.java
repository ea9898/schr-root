package moscow.ptnl.app.trace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * Логгер кол-стека, отображающий дерево вызова в иерархическом виде.
 * 
 */
public class TreeCallStackLogger {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private TreeMethodCall current;
    private TreeMethodCall root;

    public TreeCallStackLogger() {
        root = current = TreeMethodCall.root();
    }
    
    public void setExecutionId(String executionId) {
        root.setExecutionId(executionId);
    }

    public void beforeCall(String name) {
        TreeMethodCall call = TreeMethodCall.create(name, current);
        current.addNestedCall(call);
        current = call;
    }

    public void afterCall(boolean isExceptionThrown) {
        current.stopWatch();
        current = current.parent;

        if (current == root) { //вышли из корневого метода, пора писать в лог
            current.stopWatch();
            StringBuilder tree = root.toLogNode().print();

            if (isExceptionThrown) {
                tree.append(System.lineSeparator()).append("With exception thrown!!!");
            }
            logger.info("{}", tree);
            root = current = null;
        }
    }

    public boolean currentIsRoot() {
	    return current == root;
    }
	    
}
