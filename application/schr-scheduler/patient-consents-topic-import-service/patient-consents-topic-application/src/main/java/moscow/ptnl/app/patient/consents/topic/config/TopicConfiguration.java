package moscow.ptnl.app.patient.consents.topic.config;


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
                return Collections.singletonList(TopicType.PATIENT_CONSENTS_TOPIC);
            }

            @Override
            public String getParamsSuffix() {
                return "ImportService";
            }
        };
    }
}
