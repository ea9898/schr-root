package moscow.ptnl.app.esu.ecppd.handler.configuration;

import moscow.ptnl.app.ecppd.handler.task.ErpChangePatientPersonalDataProcessTask;
import moscow.ptnl.app.esu.ecppd.listener.deserializer.PatientPersonalDataDeserializer;
import moscow.ptnl.domain.repository.SettingsRepository;
import moscow.ptnl.domain.service.SettingService;
import moscow.ptnl.domain.service.SettingServiceImpl;
import moscow.ptnl.app.service.PlannersService;
import moscow.ptnl.app.service.PlannersServiceImpl;

import moscow.ptnl.schr.repository.SettingsRepositoryImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.repository.support.Repositories;

import javax.inject.Inject;

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
    public PatientPersonalDataDeserializer patientPersonalDataDeserializer() {
        return new PatientPersonalDataDeserializer();
    }

    @Bean
    public ErpChangePatientPersonalDataProcessTask erpChangePatientDoublesProcessTask() {
        return new ErpChangePatientPersonalDataProcessTask();
    }

//    @Bean
//    public Repositories repositories() { return new Repositories(applicationContext); }
}
