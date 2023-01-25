package moscow.ptnl.app.pct.handler.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.system.DiskSpaceMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import moscow.ptnl.app.metrics.SDNamingConvention;
import moscow.ptnl.app.metrics.bind.JvmBufferPoolMetrics;
import moscow.ptnl.app.metrics.bind.ProcessMemoryMetrics;
import moscow.ptnl.app.metrics.bind.ServiceMetrics;
import moscow.ptnl.app.metrics.bind.SystemMetrics;
import moscow.ptnl.app.metrics.jvm.SDClassLoaderMetrics;
import moscow.ptnl.app.metrics.jvm.SDJvmGcMetrics;
import moscow.ptnl.app.metrics.jvm.SDJvmMemoryMetrics;
import moscow.ptnl.app.metrics.jvm.SDJvmThreadMetrics;
import moscow.ptnl.app.metrics.servlet.MetricsServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;
import java.io.File;

//import moscow.ptnl.app.ws.WSService;

/**
 *
 * @author m.kachalov
 */
@Configuration
public class MetricsConfiguration {
    
    //@Autowired
    //private List<? extends WSService> services;
    
    @Bean
    public PrometheusMeterRegistry meterRegistry() {
        PrometheusMeterRegistry meterRegistry = 
                new PrometheusMeterRegistry(PrometheusConfig.DEFAULT, CollectorRegistry.defaultRegistry, Clock.SYSTEM);
        meterRegistry.config().namingConvention(new SDNamingConvention());
        
        new SDJvmThreadMetrics().bindTo(meterRegistry);
        
        new SDJvmGcMetrics().bindTo(meterRegistry);
        
        new SDClassLoaderMetrics().bindTo(meterRegistry);
        
        new JvmBufferPoolMetrics().bindTo(meterRegistry);
        
        new SDJvmMemoryMetrics().bindTo(meterRegistry);
        
        new ProcessMemoryMetrics().bindTo(meterRegistry);
        
        new SystemMetrics().bindTo(meterRegistry);
        
        new DiskSpaceMetrics(new File("/")).bindTo(meterRegistry);
        
        //new ClassLoaderMetrics().bindTo(meterRegistry);
                
        //new JvmMemoryMetrics().bindTo(meterRegistry);
                
        //new ProcessorMetrics().bindTo(meterRegistry);
        
        //new ProcessThreadMetrics().bindTo(meterRegistry);
                
        return meterRegistry;
    }
        
    @Bean
    public ServiceMetrics serviceMetrics(MeterRegistry registry) {        
        //Class<? extends WSService>[] servicesClasses =  services.stream()
        //        .map(s -> s.getClass())
        //        .collect(Collectors.toList())
        //        .toArray(new Class[services.size()]);
        ServiceMetrics metrics = new ServiceMetrics(); //заглушка
        metrics.bindTo(registry);
        return metrics;
    }
        
    @Bean
    public ServletRegistrationBean<Servlet> metricsServletRegistrationBean(PrometheusMeterRegistry service) {
        MetricsServlet metricsServlet = new MetricsServlet(service);        
        ServletRegistrationBean<Servlet> regBeen = new ServletRegistrationBean<>(metricsServlet,
                "/metrics");
        regBeen.setLoadOnStartup(4);

        return regBeen;
    }

}
