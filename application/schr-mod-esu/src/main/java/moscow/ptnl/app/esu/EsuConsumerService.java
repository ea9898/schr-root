package moscow.ptnl.app.esu;

import moscow.ptnl.domain.entity.PlannersLog;
import moscow.ptnl.app.model.PlannersEnum;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.service.PlannersService;
import moscow.ptnl.app.service.TaskManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.mos.emias.esu.lib.consumer.EsuConsumerBuilder;
import ru.mos.emias.esu.lib.consumer.EsuTopicConsumer;
import ru.mos.emias.esu.lib.consumer.message.EsuConsumerMessageProcessor;
import ru.mos.emias.esu.lib.property.EsuErrorProducerPropertiesBuilder;
import ru.mos.emias.esu.lib.property.EsuLogProducerPropertiesBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис подписки на топики и сохранения в БД сообщений от ЕСУ.
 *
 * @author sorlov
 */
@Service
public class EsuConsumerService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private String localHost = "localhost";

    private final Map<String, ConsumerWrapper> consumers = new HashMap<>();

    @Autowired(required = false)
    private List<? extends EsuConsumerProcessor<?>> esuConsumerProcessors;

    @Autowired(required = false)
    private EsuProperties esuProperties;

    @Autowired
    private TaskManagerService taskManagerService;

    @Autowired
    private PlannersService plannersService;
    
    @Autowired(required = false)
    private EsuTopicHelper topicHelper;

    @PostConstruct
    public void subscribeTopics() {
        if (topicHelper != null) {
            for (TopicType topic : topicHelper.topics()) {
                createConsumer(topic);
            }
        } else {
            LOG.warn("Не определен бин EsuTopicHelper");
        }
        try {
            localHost = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            LOG.error("Can't determine a hostname", ex);
        }
    }

    public Map<String, ConsumerWrapper> getConsumers() {
        return consumers;
    }

    @PreDestroy
    public void closeTopics() {
        getConsumers().entrySet().forEach(e -> {
            boolean result = false;

            try {
                result = e.getValue().shutdown() && e.getValue().getEntityId() != null;
            } catch (Exception ex) {
                LOG.error("Не удалось остановить подписчика топика: " + e.getKey(), ex);
            }
            if (result) {
                plannersService.stopPlanner(e.getValue().getEntityId(), null);
            }
        });
    }

    /**
     * Управляет включением-отключением консумеров на основе настроек из CONFIG.
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 5000)
    private void manageTopics() {
        LOG.info("START MANAGING TOPICS");

        getConsumers().entrySet().forEach(e -> {
            String topic = e.getKey();
            ConsumerWrapper consumer = e.getValue();

            if (taskManagerService.checkConsumerIsOn(e.getValue().getTopicName() + topicHelper.getParamsSuffix())) {
                PlannersEnum planner = e.getValue().getTopic().getPlanner();

                try {
                    if (consumer.enable()) {
                        consumer.setEntityId(plannersService.startPlanner(planner).getId());
                    }
                    else if (consumer.getEntityId() != null) {
                        //6. Система проверяет, что пришло время записи в журнал
                        PlannersLog log = plannersService.updateMonitoringDate(consumer.getEntityId(), planner);
                        consumer.setEntityId(log.getId());
                    }
                } catch (Exception ex) {
                    LOG.error("Ошибка запуска подписчика топика : " + topic, ex);
                    plannersService.savePlannerError(planner, ex.getMessage());
                }
            } else {
                boolean result = true;
                String error = null;

                try {
                    result = consumer.disable() && consumer.getEntityId() != null;
                } catch (Exception ex) {
                    LOG.error("Ошибка приостановки подписчика топика: " + topic, ex);
                    error = ex.getMessage();
                }
                if (result) {
                    plannersService.stopPlanner(consumer.getEntityId(), error);

                }
                consumer.setEntityId(null);
            }
        });
    }

    private void createConsumer(TopicType topic) {
        if (esuConsumerProcessors == null) {
            LOG.warn("ЕСУ - не найден ни один обработчик топиков");
            return;
        }
        try {            
            EsuConsumerMessageProcessor processor = esuConsumerProcessors.stream()
                    .filter(p -> topic.equals(p.getTopicType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No processor for topic=" + topic.getName()));
            moscow.ptnl.app.esu.ConsumerWrapper consumer = new moscow.ptnl.app.esu.ConsumerWrapper(buildConsumer(topic.getName(), processor), topic.getName(), topic, LOG);
            getConsumers().put(topic.getName(), consumer);
            LOG.info("Создан подписчик топика: {}", topic.getName());
        } catch (Exception ex) {
            LOG.error("Не удалось создать подписчика на ЕСУ топик: " + topic.getName(), ex);
        }
    }

    private EsuTopicConsumer buildConsumer(String topicName, EsuConsumerMessageProcessor processor) {
        if (esuProperties == null) {
            throw new IllegalStateException("Не определен бин EsuProperties");
        }
        EsuConsumerBuilder builder = 
            (new EsuConsumerBuilder(
                    esuProperties.getBootstrapServersESU(), // Сервера для подключения к ЕСУ Kafka
                    esuProperties.getConsumerGroupId(), // Идентификатор подписчика
                    topicName, // Название топика
                    esuProperties.getTopicThreadsNumber() // Кол-во потоков
            ))
            .withProcessor(processor) // Объект класса, унаследованного от EsuConsumerMessageProcessor. NOT THREAD SAFE
            .withPollingInterval(esuProperties.getPollingInterval()) // Интервал между запросами в Kafka в мс (по-умолчанию 300)
            .withPollingTimeout(esuProperties.getPollingTimeout()) // Таймаут чтения сообщений из Kafka в мс (по-умолчанию 300)
            .withCustomErrorProducerProperties(new EsuErrorProducerPropertiesBuilder().withRequestTimeout(esuProperties.getProducerTimeout()).build())  // Таймаут продюсера, при отправке сообщений в топик ConsumerErrors
            /*
                Боремся с ошибкой: 
                Commit cannot be completed since the group has already rebalanced and assigned the partitions to another member
                ребаланс по таймауту
                https://coderoad.ru/43991845/Kafka10-1-heartbeat-interval-ms-session-timeout-ms-%D0%B8-max-poll-interval-ms
            */
            .withCustomProperty("session.timeout.ms", 30000) //default 3 seconds, но в разных примерах в Интернет ставят около 30 сек
            .withCustomProperty("heartbeat.interval.ms", 10000) //рекомендуют треть от session.timeout.ms
            .withCustomProperty("max.poll.interval.ms", 270000); //default 30 seconds, увеличим до 270 сек (возможно у нас что-то с транзакциями и вставка в залоченную таблицу подвисает)

        if (esuProperties.isLogEnabled()) {
            builder = builder.withCustomLogProducerProperties( //логирование консумера отправкой метрик в специальные топики Kafka
                new EsuLogProducerPropertiesBuilder(esuProperties.getLogServers(), esuProperties.getMetricMessageProduct())
                    .enabled(esuProperties.isLogEnabled())
                    .withProduct(esuProperties.getMetricMessageProduct())
                    .build()
            );
        }
        return builder.build();
    }
}
