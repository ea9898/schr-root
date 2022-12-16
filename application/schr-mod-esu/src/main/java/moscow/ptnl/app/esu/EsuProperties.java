package moscow.ptnl.app.esu;

import moscow.ptnl.domain.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 *
 * @author m.kachalov
 */
@Component
@PropertySource("classpath:esu.properties")
public class EsuProperties {
    
    @Value("${esu.consumer.group.id}")
    private String consumerGroupId;

    @Value("${esu.consumer.topic.threads.number:1}")
    private Integer topicThreadsNumber;

    @Value("${esu.consumer.polling.interval:300}")
    private Integer pollingInterval;

    @Value("${esu.consumer.polling.timeout:300}")
    private Integer pollingTimeout;

    @Value("${esu.consumer.error.producer.timeout:600}")
    private Integer producerTimeout;

    @Value("${esu.consumer.max.poll.records:1}")
    private Integer maxPollRecords;

    @Value("${esu.consumer.retries.limit:5}")
    private Integer retriesLimit;

    @Value("${esu.bootstrap.servers}")
    private String esuServers;
    
    @Value("${esu.log.producer.enabled:true}")
    private Boolean logEnabled; //разрешить логирование работы в специальный топик Kafka
    
    @Value("${esu.log.producer.servers:}") 
    private String logServers; //сервера куда отправляются лог сообщения
    
    @Value("${esu.log.producer.metric.message.product:}")
    private String metricMessageProduct; //идентификатор продукта для логирования
    
    @Autowired
    private SettingService settingService;

    public String getConsumerGroupId() {
        return consumerGroupId;
    }

    public Integer getTopicThreadsNumber() {
        return topicThreadsNumber;
    }

    public Integer getPollingInterval() {
        return settingService.getEmptyQueueWaitTimeESU() == null ? pollingInterval : settingService.getEmptyQueueWaitTimeESU().intValue();
    }

    public Integer getPollingTimeout() {
        return settingService.getReadTimeoutESU() == null ? pollingTimeout : settingService.getReadTimeoutESU().intValue();
    }

    public Integer getProducerTimeout() {
        return producerTimeout;
    }

    public String getBootstrapServersESU() { return esuServers; }
    
    public boolean isLogEnabled() {
        return Boolean.TRUE.equals(logEnabled);
    }
    
    public String getLogServers() {
        return logServers;
    }

    public String getMetricMessageProduct() {
        return metricMessageProduct;
    }

    public int getRetriesLimit() {
        return retriesLimit;
    }

    public int getMaxPollRecords() {
        return maxPollRecords;
    }
}
