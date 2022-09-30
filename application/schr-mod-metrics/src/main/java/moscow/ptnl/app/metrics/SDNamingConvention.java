package moscow.ptnl.app.metrics;

import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.prometheus.PrometheusNamingConvention;

import java.util.regex.Pattern;

/**
 *
 * @author m.kachalov
 */
public class SDNamingConvention extends PrometheusNamingConvention {
    
   private static final Pattern nameChars = Pattern.compile("[^a-zA-Z0-9_:]");

   @Override
   public String name(String name, Type type, String baseUnit) {
      String conventionName = NamingConvention.snakeCase.name(name, type, baseUnit);
      switch(type) {
      case COUNTER:
         if (!conventionName.endsWith("_total")) {
            conventionName = conventionName + "_total";
         }
         break;
      case TIMER:
      case LONG_TASK_TIMER:
         conventionName = conventionName + "_seconds";
      }

      String sanitized = nameChars.matcher(conventionName).replaceAll("_");
      if (!Character.isLetter(sanitized.charAt(0))) {
         sanitized = "m_" + sanitized;
      }

      return sanitized;
   }
}
