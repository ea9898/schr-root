package moscow.ptnl.app.esu.ae.handler.configuration;

import moscow.ptnl.app.esu.aei.listener.processor.AttachmentEventProcessor;
import moscow.ptnl.app.esu.aei.listener.task.AttachmentEventProcessTask;
import moscow.ptnl.app.esu.aei.listener.validator.AttachmentEventValidator;
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
    public AttachmentEventProcessTask attachmentEventProcessTask() {
        return new AttachmentEventProcessTask();
    }

    @Bean
    public AttachmentEventProcessor attachmentEventProcessor() { return new AttachmentEventProcessor(); }

    @Bean
    public AttachmentEventValidator attachmentEventValidator() { return new AttachmentEventValidator(); }
//    @Bean
//    public Repositories repositories() { return new Repositories(applicationContext); }
}
