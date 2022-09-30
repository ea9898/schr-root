package moscow.ptnl.app.metrics.bind;

import io.github.mweirauch.micrometer.jvm.extras.procfs.ProcfsStatus;
import io.github.mweirauch.micrometer.jvm.extras.procfs.ProcfsStatus.KEY;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.util.Locale;
import java.util.Objects;

/**
 * Original: io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics
 * 
 * @author m.kachalov
 */
public class ProcessMemoryMetrics implements MeterBinder {
    
    private final ProcfsStatus status;

    public ProcessMemoryMetrics() {
        this.status = ProcfsStatus.getInstance();
    }

    /* default */ ProcessMemoryMetrics(ProcfsStatus status) {
        this.status = Objects.requireNonNull(status);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        for (final KEY key : KEY.values()) {
            final String name = mapName(key);
            Gauge.builder(name, status, statusRef -> value(key))//
                    .baseUnit("bytes")//
                    .register(registry);
        }
    }

    private Double value(KEY key) {
        return status.get(key);
    }
    
    /**
     * Мапит имена свойств в соответстви  с постановкой задачи.
     * 
     * @param key
     * @return 
     */
    protected String mapName(KEY key) {
        StringBuilder name = new StringBuilder("process.");
        switch (key) {
            case VSS:
                name.append("virtual");
                break;
            case RSS:
                name.append("resident");
                break;
            default:
                name.append(key.name().toLowerCase(Locale.ENGLISH));
        }
        name.append(".memory");
        return name.toString();
    }
    
}
