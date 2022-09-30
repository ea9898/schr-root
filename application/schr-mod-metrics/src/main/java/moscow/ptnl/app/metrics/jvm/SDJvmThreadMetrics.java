/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moscow.ptnl.app.metrics.jvm;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 *
 * @author mkachalov
 */
public class SDJvmThreadMetrics implements MeterBinder {
    
    private final ThreadMXBean threadMXBean;
    
    public SDJvmThreadMetrics() {
        threadMXBean = ManagementFactory.getThreadMXBean();
    }
    
    private static double nullSafeArrayLength(long[] array) {
        return null == array ? 0.0D : (double)array.length;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        
        Gauge.builder("jvm.threads.current", this.threadMXBean, ThreadMXBean::getThreadCount)
                .description("Current thread count of a JVM")
                .register(registry);
        
        Gauge.builder("jvm.threads.daemon", this.threadMXBean, ThreadMXBean::getDaemonThreadCount)
                .description("Daemon thread count of a JVM")
                .register(registry);
        
        Gauge.builder("jvm.threads.peak", this.threadMXBean, ThreadMXBean::getPeakThreadCount)
                .description("Peak thread count of a JVM")
                .register(registry);
        
        Gauge.builder("jvm.threads.started.total", this.threadMXBean, ThreadMXBean::getTotalStartedThreadCount)
                .description("Started thread count of a JVM")
                .register(registry);
        
        Gauge.builder("jvm.threads.started.deadlocked", this.threadMXBean, (x) -> {
            return nullSafeArrayLength(x.findDeadlockedThreads());
        })
                .description("Cycles of JVM-threads that are in deadlock waiting to acquire object monitors or ownable synchronizers")
                .register(registry);
        
        Gauge.builder("jvm.threads.started.deadlocked.monitor", this.threadMXBean, (x) -> {
            return nullSafeArrayLength(x.findMonitorDeadlockedThreads());
        })
                .description("Cycles of JVM-threads that are in deadlock waiting to acquire object monitors")
                .register(registry);
    }

    
    
    


    
}
