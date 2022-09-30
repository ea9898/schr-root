/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moscow.ptnl.app.metrics.jvm;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author mkachalov
 */
public class SDJvmGcMetrics extends JvmGcMetrics {
    
   private final List<GarbageCollectorMXBean> garbageCollectorMXBeans;
   
   public SDJvmGcMetrics() {
       garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
   }

   @Override
   public void bindTo(MeterRegistry registry) {
      Iterator var2 = this.garbageCollectorMXBeans.iterator();

      while(var2.hasNext()) {
          
         GarbageCollectorMXBean gc = (GarbageCollectorMXBean)var2.next();
         
         Gauge.builder("jvm_gc_collection_seconds_count", gc, GarbageCollectorMXBean::getCollectionCount)
                 .tag("gc", gc.getName())
                 .register(registry);
         Gauge.builder("jvm_gc_collection_seconds_sum", gc, (x) -> {
                        return (double) x.getCollectionTime() / 1000.0;
                    })
                 .tag("gc", gc.getName())
                 .register(registry);
      }

   }
   
}
