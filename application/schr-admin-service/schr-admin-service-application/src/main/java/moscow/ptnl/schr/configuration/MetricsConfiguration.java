package moscow.ptnl.schr.configuration;

import io.micrometer.core.instrument.Clock;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import jakarta.servlet.Servlet;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author m.kachalov
 */
@Configuration
public class MetricsConfiguration {

    private final static Logger LOG = LoggerFactory.getLogger(MetricsConfiguration.class);

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

        return meterRegistry;
    }

    @Bean
    public ServletRegistrationBean<Servlet> metricsServletRegistrationBean(PrometheusMeterRegistry service) {
        MetricsServlet metricsServlet = new MetricsServlet(service);
        ServletRegistrationBean<Servlet> regBeen = new ServletRegistrationBean<>(metricsServlet,
                "/schr/metrics");
        regBeen.setLoadOnStartup(4);

        return regBeen;
    }

    @Bean
    public ServiceMetrics serviceMetrics() {
        return new ServiceMetrics();
    }
}
