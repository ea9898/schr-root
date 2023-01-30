package moscow.ptnl.app.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 *
 * @author m.kachalov
 */
@Configuration
@EnableElasticsearchRepositories(
    basePackages = {
        "moscow.ptnl.schr.repository.es",
        "moscow.ptnl.app.repository.es",
        "moscow.ptnl.app.infrastructure.repository.es"
    }
)
@PropertySource("classpath:elasticsearch.properties")
public class ESConfiguration extends ElasticsearchConfigurationSupport {
    
    @Value("${elasticsearch.host:localhost}")
    private String esHost;

    @Value("${elasticsearch.port:9200}")
    private int esPort;

    @Bean(destroyMethod = "close")
    public RestClient restClient() {
        return RestClient.builder(new HttpHost(esHost, esPort, "http"))
                //.setMetaHeaderEnabled(false)
                //.setNodeSelector(NodeSelector.ANY)
                .build();
    }

    @Bean
    public ElasticsearchTransport elasticsearchTransport(RestClient restClient) {
        return new RestClientTransport(restClient, new JacksonJsonpMapper());
    }
     
    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate(
            ElasticsearchClient client, 
            ElasticsearchConverter converter
    ) {
        return new ElasticsearchTemplate(client, converter);
    }

}
