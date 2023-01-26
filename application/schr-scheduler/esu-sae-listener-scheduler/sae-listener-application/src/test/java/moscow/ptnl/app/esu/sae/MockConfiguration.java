package moscow.ptnl.app.esu.sae;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import moscow.ptnl.app.esu.sae.listener.deserializer.PatientSchoolAttachmentDeserializer;
import moscow.ptnl.app.repository.es.IndexEsuInputRepository;
import moscow.ptnl.app.service.PlannersService;
import moscow.ptnl.app.service.PlannersServiceImpl;
import moscow.ptnl.domain.repository.SettingsRepository;
import moscow.ptnl.domain.service.SettingService;
import moscow.ptnl.domain.service.SettingServiceImpl;
import moscow.ptnl.schr.repository.SettingsRepositoryImpl;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    public IndexEsuInputRepository indexEsuInputRepository;

    @Bean
    public PatientSchoolAttachmentDeserializer patientSchoolAttachmentDeserializer() {
        return new PatientSchoolAttachmentDeserializer();
    }

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

}
