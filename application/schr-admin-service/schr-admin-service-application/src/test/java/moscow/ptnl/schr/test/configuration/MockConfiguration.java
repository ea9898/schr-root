package moscow.ptnl.schr.test.configuration;

import jakarta.inject.Inject;
import moscow.ptnl.domain.repository.SettingsRepository;
import moscow.ptnl.domain.service.SettingService;
import moscow.ptnl.domain.service.SettingServiceImpl;
import moscow.ptnl.schr.repository.SettingsRepositoryImpl;
import moscow.ptnl.schr.service.AdminService;
import moscow.ptnl.schr.service.AdminServiceImpl;
import moscow.ptnl.schr.service.ws.AnthropometryServiceImpl;
import moscow.ptnl.schr.service.ws.ConsentInfoServiceImpl;
import moscow.ptnl.schr.service.ws.PatientServiceImpl;
import moscow.ptnl.schr.service.ws.SchoolServiceImpl;
import moscow.ptnl.schr.ws.interceptors.trace.TraceInInterceptor;
import moscow.ptnl.schr.ws.interceptors.trace.TraceInterceptorService;
import moscow.ptnl.schr.ws.interceptors.trace.TraceOutInterceptor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableAspectJAutoProxy
@PropertySource("classpath:application.properties")
public class MockConfiguration {

    @Inject
    private ApplicationContext applicationContext;

    @Bean
    public SettingsRepository settingsRepository() {
        return new SettingsRepositoryImpl();
    }

    @Bean
    public SettingService settingService() {
        return new SettingServiceImpl();
    }

    @Bean
    public AdminService adminService() {
        return new AdminServiceImpl();
    }

    @Bean
    public TraceOutInterceptor outInterceptor() {
        return new TraceOutInterceptor();
    }

    @Bean
    public TraceInInterceptor inInterceptor(){
        return new TraceInInterceptor();
    }

    @Bean
    public TraceInterceptorService metricsInterceptorService() {
        return new TraceInterceptorService();
    }

    @Bean
    public PatientServiceImpl patientService() {
        return new PatientServiceImpl();
    }

    @Bean
    public AnthropometryServiceImpl anthropometryService() {
        return new AnthropometryServiceImpl();
    }

    @Bean
    public ConsentInfoServiceImpl consentInfoService() {
        return new ConsentInfoServiceImpl();
    }

    @Bean
    public SchoolServiceImpl schoolService() {
        return new SchoolServiceImpl();
    }
}
