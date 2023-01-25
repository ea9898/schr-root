package moscow.ptnl.app.esu.sae.handler.configuration;

import javax.inject.Inject;
import moscow.ptnl.app.sae.handler.task.SchoolAttachmentEventProcessTask;
import moscow.ptnl.app.esu.sae.listener.deserializer.PatientSchoolAttachmentDeserializer;
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
    public PatientSchoolAttachmentDeserializer patientSchoolAttachmentDeserializer() {
        return new PatientSchoolAttachmentDeserializer();
    }

    @Bean
    public SchoolAttachmentEventProcessTask schoolAttachmentEventProcessTask() {
        return new SchoolAttachmentEventProcessTask();
    }

//    @Bean
//    public Repositories repositories() { return new Repositories(applicationContext); }
}
