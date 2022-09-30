/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moscow.ptnl.app.metrics.bind;

import com.sun.management.UnixOperatingSystemMXBean;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.TimeUnit;

/**
 * Нобходимые разрешения для WildFly 10:
 * 
 *   &lt;module name="sun.scripting" export="true"/&gt;
 *   &lt;system export="true"&gt;
 *       &lt;paths&gt;
 *           &lt;path name="com/sun/management"/&gt;
 *       &lt;/paths&gt;
 *       &lt;exports&gt;
 *           &lt;include-set&gt;
 *               &lt;path name="META-INF/services"/&gt;
 *           &lt;/include-set&gt;
 *       &lt;/exports&gt;
 *   &lt;/system&gt;
 * 
 * Не используем FileDescriptorMetrics, так как имена параметров не соответствуют
 * постановке.
 *
 * @author mkachalov
 */
public class SystemMetrics implements MeterBinder {
    
    private final OperatingSystemMXBean os;
    private final RuntimeMXBean runtimeMXBean;
    
    public SystemMetrics(){
        this.os = ManagementFactory.getOperatingSystemMXBean();
        this.runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("process_open_fds", this, value -> value.getOpenFileDescriptorCount())            
            .description("The open file descriptor count")
            .register(registry);
        
        Gauge.builder("process_max_fds", this, value -> value.getMaxFileDescriptorCount())
            .description("The maximum file descriptor count")
            .register(registry);
        
        TimeGauge.builder("process_cpu_seconds_total", this, TimeUnit.SECONDS, value -> value.getProcessCpuTime() / 1000)
            .description("Number of ticks executing code of that process")
            .register(registry);
        
        TimeGauge.builder("process_uptime", runtimeMXBean, TimeUnit.SECONDS, value -> value.getUptime() / 1000)
            .description("The uptime of the Java virtual machine")
            .register(registry);

        TimeGauge.builder("process_start_time", runtimeMXBean, TimeUnit.SECONDS, value -> value.getStartTime() / 1000)
            .description("Start time of the process since unix epoch.")
            .register(registry);
        
        Gauge.builder("jvm_info", () -> 1.0)
            .tag("version", System.getProperty("java.runtime.version", "unknown"))
            .tag("vendor", System.getProperty("java.vm.vendor", "unknown"))
            .tag("runtime", System.getProperty("java.runtime.name", "unknown"))
            .description("JVM version info")
            .register(registry); 
        
        //process_virtual_memory_bytes
        //process_resident_memory_bytes
        //Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        // https://github.com/mweirauch/micrometer-jvm-extras
        
        
    }
    
    private long getOpenFileDescriptorCount() {
        if(os instanceof UnixOperatingSystemMXBean){
            return ((UnixOperatingSystemMXBean) os).getOpenFileDescriptorCount();
        }
        return -1;
    }
    
    private long getMaxFileDescriptorCount() {
        if(os instanceof UnixOperatingSystemMXBean){
            return ((UnixOperatingSystemMXBean) os).getMaxFileDescriptorCount();
        }
        return -1;
    }
    
    private long getProcessCpuTime() {
        if(os instanceof UnixOperatingSystemMXBean){
            return ((UnixOperatingSystemMXBean) os).getProcessCpuTime();
        }
        return -1;
    }
    
}
