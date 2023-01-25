package moscow.ptnl.app.erp.handler.configuration;

import moscow.ptnl.app.ecphhs.handler.task.ErpChangePatientPoliciesProcessTask;
import moscow.ptnl.app.ecppis.deserilizer.ErpChangePatientPoliciesDeserializer;
import moscow.ptnl.app.service.PlannersService;
import moscow.ptnl.app.service.PlannersServiceImpl;
import moscow.ptnl.domain.repository.SettingsRepository;
import moscow.ptnl.domain.service.SettingService;
import moscow.ptnl.domain.service.SettingServiceImpl;
import moscow.ptnl.schr.repository.SettingsRepositoryImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import jakarta.inject.Inject;

/**
 *
 * @author sorlov
 */
@Configuration
@EnableAspectJAutoProxy
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
    public PlannersService plannersService() {
        return new PlannersServiceImpl();
    }

    @Bean
    public ErpChangePatientPoliciesDeserializer erpChangePatientPoliciesDeserializer() {
        return new ErpChangePatientPoliciesDeserializer();
    }

    @Bean
    public ErpChangePatientPoliciesProcessTask erpChangePatientPoliciesProcessTask() {
        return new ErpChangePatientPoliciesProcessTask();
    }

//    @Bean
//    public Repositories repositories() { return new Repositories(applicationContext); }
}
