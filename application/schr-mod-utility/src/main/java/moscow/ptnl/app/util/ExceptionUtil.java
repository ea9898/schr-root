package moscow.ptnl.app.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author m.kachalov
 */
public class ExceptionUtil {
    
    private ExceptionUtil(){}
    
    public static String getStackTrace(Throwable exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
    
}
