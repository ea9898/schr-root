package moscow.ptnl.app.erp.last.anthropometry.config;


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
                return Collections.singletonList(TopicType.LAST_ANTHROPOMETRY);
            }

            @Override
            public String getParamsSuffix() {
                return "ImportService";
            }
        };
    }
}
