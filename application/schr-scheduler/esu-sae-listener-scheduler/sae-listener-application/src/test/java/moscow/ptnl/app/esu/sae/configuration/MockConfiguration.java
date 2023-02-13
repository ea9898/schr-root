package moscow.ptnl.app.esu.sae.configuration;

import jakarta.inject.Inject;
import moscow.ptnl.app.esu.sae.listener.deserializer.PatientSchoolAttachmentDeserializer;
import moscow.ptnl.app.esu.sae.listener.service.AnthropometryServiceImpl;
import moscow.ptnl.app.esu.sae.listener.service.ConsentInfoServiceImpl;
import moscow.ptnl.app.esu.sae.listener.service.PatientServiceImpl;
import moscow.ptnl.app.esu.sae.listener.service.anthropometry.AnthropometryService;
import moscow.ptnl.app.esu.sae.listener.service.consent.ConsentInfoService;
import moscow.ptnl.app.esu.sae.listener.service.patient.PatientService;
import moscow.ptnl.app.esu.sae.listener.task.SchoolAttachmentEventProcessTask;
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

    @Bean
    public PatientService patientService() {
        return new PatientServiceImpl();
    }

    @Bean
    public AnthropometryService anthropometryService() {
        return new AnthropometryServiceImpl();
    }

    @Bean
    public ConsentInfoService consentInfoService() {
        return new ConsentInfoServiceImpl();
    }
}
