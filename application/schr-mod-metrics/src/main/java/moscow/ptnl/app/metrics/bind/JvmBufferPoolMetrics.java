/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moscow.ptnl.app.metrics.bind;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author mkachalov
 */
@SuppressWarnings("unchecked")
public class JvmBufferPoolMetrics implements MeterBinder {
    
    private final List<BufferPoolMXBean> bufferPoolMXBeans = new ArrayList();
    
    public JvmBufferPoolMetrics() {
        this.bufferPoolMXBeans.addAll(getBufferPools());
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Iterator var2 = this.bufferPoolMXBeans.iterator();

        while(var2.hasNext()) {
            BufferPoolMXBean bean = (BufferPoolMXBean) var2.next();
            String name = bean.getName();
         
            Gauge.builder("jvm_buffer_pool_used_bytes", bean, b -> (double) b.getMemoryUsed())
                    .description("Used bytes of a given JVM buffer pool.")
                    .tag("pool", name)
                    .register(registry);
            
            Gauge.builder("jvm_buffer_pool_capacity_bytes", bean, b -> (double) b.getTotalCapacity())
                    .description("Bytes capacity of a given JVM buffer pool.")
                    .tag("pool", name)
                    .register(registry);
         
            Gauge.builder("jvm_buffer_pool_used_buffers", bean, b -> b.getCount())
                    .description("Used buffers of a given JVM buffer pool.")
                    .tag("pool", name)
                    .register(registry);
      }
      
    }
    
    private List<BufferPoolMXBean> getBufferPools() {
        return ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
   }
    
}
