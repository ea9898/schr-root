package moscow.ptnl.app.esu.aei.listener.config;

import moscow.ptnl.app.esu.EsuTopicHelper;
import moscow.ptnl.app.model.TopicType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author m.kachalov
 */
@Configuration
public class TopicConfiguration {

    @Bean
    public EsuTopicHelper esuTopicHelper() {
        return new EsuTopicHelper() {

            @Override
            public List<TopicType> topics() {
                return Collections.singletonList(TopicType.ATTACHMENT_EVENT);
            }

            @Override
            public String getParamsSuffix() {
                return "ImportService";
            }
        };
    }
}
