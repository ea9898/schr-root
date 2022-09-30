/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moscow.ptnl.app.metrics.jvm;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author mkachalov
 */
public class SDJvmMemoryMetrics extends JvmMemoryMetrics {
    
   private final MemoryMXBean memoryBean;
   private final List<MemoryPoolMXBean> poolBeans;
   
   public SDJvmMemoryMetrics() {
       memoryBean = ManagementFactory.getMemoryMXBean();
       poolBeans = ManagementFactory.getMemoryPoolMXBeans();
   }

   @Override
    public void bindTo(MeterRegistry registry) {
        
        Gauge.builder("jvm_memory_bytes_used", this.memoryBean, b -> (double) b.getHeapMemoryUsage().getUsed())
                .description("Used bytes of a given JVM memory area.")
                .tag("area", "heap")
                .register(registry);
        
        Gauge.builder("jvm_memory_bytes_committed", this.memoryBean, b -> (double) b.getHeapMemoryUsage().getCommitted())
                .description("Committed (bytes) of a given JVM memory area.")
                .tag("area", "heap")
                .register(registry);
        
        Gauge.builder("jvm_memory_bytes_max", this.memoryBean, b -> (double) b.getHeapMemoryUsage().getMax())
                .description("Max (bytes) of a given JVM memory area.")
                .tag("area", "heap")
                .register(registry);
        
        Gauge.builder("jvm_memory_bytes_init", this.memoryBean, b -> (double) b.getHeapMemoryUsage().getInit())
                .tag("area", "heap")
                .register(registry);
        
        Gauge.builder("jvm_memory_bytes_used", this.memoryBean, b -> (double) b.getNonHeapMemoryUsage().getUsed())
                .description("Used bytes of a given JVM memory area.")
                .tag("area", "nonheap")
                .register(registry);
        
        Gauge.builder("jvm_memory_bytes_committed", this.memoryBean, b -> (double) b.getNonHeapMemoryUsage().getCommitted())
                .description("Committed (bytes) of a given JVM memory area.")
                .tag("area", "nonheap")
                .register(registry);
        
        Gauge.builder("jvm_memory_bytes_max", this.memoryBean, b -> (double) b.getNonHeapMemoryUsage().getMax())
                .description("Max (bytes) of a given JVM memory area.")
                .tag("area", "nonheap")
                .register(registry);
        
        Gauge.builder("jvm_memory_bytes_init", this.memoryBean, b -> (double) b.getNonHeapMemoryUsage().getInit())
                .description("Initial bytes of a given JVM memory area.")
                .tag("area", "nonheap")
                .register(registry);

        Iterator pb = this.poolBeans.iterator();

        while(pb.hasNext()) {
            MemoryPoolMXBean poolBean = (MemoryPoolMXBean) pb.next();
            
            Gauge.builder("jvm_memory_pool_bytes_used", poolBean, b -> (double) b.getUsage().getUsed())
                    .description("Used bytes of a given JVM memory pool.")
                    .tag("pool", poolBean.getName())
                    .register(registry);
            
            Gauge.builder("jvm_memory_pool_bytes_committed", poolBean, b -> (double) b.getUsage().getCommitted())
                    .description("Committed bytes of a given JVM memory pool.")
                    .tag("pool", poolBean.getName())
                    .register(registry);
            
            Gauge.builder("jvm_memory_pool_bytes_max", poolBean, b -> (double) b.getUsage().getMax())
                    .description("Max bytes of a given JVM memory pool.")
                    .tag("pool", poolBean.getName())
                    .register(registry);
            
            Gauge.builder("jvm_memory_pool_bytes_init", poolBean, b -> (double) b.getUsage().getMax())
                    .description("Initial bytes of a given JVM memory pool.")
                    .tag("pool", poolBean.getName())
                    .register(registry);
        }

    }
}
