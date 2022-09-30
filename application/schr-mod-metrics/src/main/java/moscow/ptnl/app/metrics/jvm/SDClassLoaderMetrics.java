/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moscow.ptnl.app.metrics.jvm;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

/**
 *
 * @author mkachalov
 */
public class SDClassLoaderMetrics extends ClassLoaderMetrics {
    
   private final ClassLoadingMXBean clBean;
   
   public SDClassLoaderMetrics() {
       clBean = ManagementFactory.getClassLoadingMXBean();
   }

   @Override
   public void bindTo(MeterRegistry registry) {
       
      Gauge.builder("jvm.classes.loaded", this.clBean, ClassLoadingMXBean::getLoadedClassCount)
              .description("The number of classes that are currently loaded in the JVM")
              .register(registry);
      
      Gauge.builder("jvm_classes_loaded_total", this.clBean, ClassLoadingMXBean::getTotalLoadedClassCount)
              .description("The total number of classes that have been loaded since the JVM has started execution")
              .register(registry);
      
      Gauge.builder("jvm_classes_unloaded_total", this.clBean, ClassLoadingMXBean::getUnloadedClassCount)
              .description("The total number of classes that have been unloaded since the JVM has started execution")
              .register(registry);
   }
}
