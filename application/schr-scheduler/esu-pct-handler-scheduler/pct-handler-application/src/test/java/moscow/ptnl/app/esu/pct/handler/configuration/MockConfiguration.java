package moscow.ptnl.app.esu.pct.handler.configuration;

import deserializer.PatientConsentsTopicDeserializer;
import moscow.ptnl.app.pct.handler.task.PatientConsentsTopicProcessTask;
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
    public PatientConsentsTopicDeserializer patientConsentsTopicDeserializer() {
        return new PatientConsentsTopicDeserializer();
    }

    @Bean
    public PatientConsentsTopicProcessTask patientConsentsTopicProcessTask() {
        return new PatientConsentsTopicProcessTask();
    }

//    @Bean
//    public Repositories repositories() { return new Repositories(applicationContext); }
}
