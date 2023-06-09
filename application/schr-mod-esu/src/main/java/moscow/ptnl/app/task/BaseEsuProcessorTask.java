package moscow.ptnl.app.task;

import moscow.ptnl.app.config.PersistenceConstraint;
import moscow.ptnl.app.domain.model.es.IndexEsuInput;
import moscow.ptnl.app.repository.es.IndexEsuInputRepository;
import moscow.ptnl.domain.entity.PlannersLog;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.repository.EsuInputCRUDRepository;
import moscow.ptnl.app.scheduling.BaseTask;
import moscow.ptnl.domain.entity.esu.EsuInput;
import moscow.ptnl.domain.entity.esu.EsuInput_;
import moscow.ptnl.domain.entity.esu.EsuStatusType;
import moscow.ptnl.domain.service.SettingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class BaseEsuProcessorTask extends BaseTask {

    protected static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Value("${esu.consumer.group.id:}")
    protected String consumerGroupId;

    @PersistenceContext(unitName = PersistenceConstraint.PU_NAME)
    protected EntityManager entityManager;

    @Autowired
    protected SettingService settingService;

    @Autowired
    protected EsuInputCRUDRepository esuInputCRUDRepository;

    @Autowired
    protected IndexEsuInputRepository indexEsuInputRepository;

    protected abstract TopicType getTopic();

    protected abstract Optional<String> processMessage(String inputMsg);

    @Override
    protected final boolean work(PlannersLog plannerLog) {
        String error = null;
        List<EsuInput> inputs = null;

        try {
            while (true) {
                try {
                    inputs = transactions.execute(s -> {
                        Long resetInterval = settingService.getSettingProperty(getPlanner().getPlannerName()
                                + ".resetInterval", Long.class, false);
                        PageRequest paging = PageRequest.of(0,
                                settingService.getSettingProperty(getPlanner().getPlannerName()
                                        + ".numberMsg", Integer.class, false),
                                Sort.by(Sort.Direction.ASC, EsuInput_.DATE_CREATED));
                        //3. Выполняет отбор (SELECT FOR UPDATE) первых N (*.numberMsg) записей,
                        // отсортированных в порядке возрастания по полю DATE_UPDATED в ESU_INPUT по условию
                        List<EsuInput> messages = esuInputCRUDRepository.find(getTopic().getName(),
                                getTopic().getName() + "." + consumerGroupId,
                                EsuStatusType.NEW, EsuStatusType.IN_PROGRESS, LocalDateTime.now().minusHours(resetInterval), paging);
                        messages.forEach(m -> {
                            //Если найдена хотя бы одна запись, то Система обновляет статус отобранных записей
                            m.setStatus(EsuStatusType.IN_PROGRESS);
                            m.setError(null);
                            m.setDateUpdated(LocalDateTime.now());
                        });
                        return messages;
                    });
                    if (inputs == null || inputs.isEmpty()) {
                        //переход на шаг 3 через интервал времени,  указанный в *.emptyQueueWaitTime
                        Thread.sleep(settingService.getSettingProperty(getPlanner().getPlannerName()
                                + ".emptyQueueWaitTime", Long.class, false));
                    } else {
                        //4.
                        for (EsuInput input : inputs) {
                            try {
                                //4.1 Система по значению ESU_INPUT.ES_ID из обрабатываемой записи получает документ
                                // из индекса index_esu_input и забирает сообщение из поля _source.message
                                Optional<IndexEsuInput> indexEsuInput = indexEsuInputRepository.findById(input.getEsId());
                                if (indexEsuInput.isPresent()) {
                                    Optional<String> processingError = processMessage(indexEsuInput.get().getMessage());

                                    if (processingError.isPresent()) {
                                        //АС.2
                                        input.setError(processingError.get());
                                        input.setStatus(EsuStatusType.PROCESSED);
                                    }
                                    else if (input.getStatus() == EsuStatusType.IN_PROGRESS) {
                                        input.setError(null);
                                        input.setStatus(EsuStatusType.PROCESSED);
                                    }
                                    input.setDateUpdated(LocalDateTime.now());
                                } else {
                                    input.setError("Не найдено сообщение в индексе с id=" + input.getEsId());
                                    input.setStatus(EsuStatusType.PROCESSED);
                                }
                            } finally {
                                transactions.executeWithoutResult(status -> esuInputCRUDRepository.save(input));
                            }
                        }
                    }
                } catch (InterruptedException ex) {
                    //Просто завершаем выполнение
                    error = "Interrupted";
                    return false;
                } catch (IllegalStateException ex) {
                    error = ex.getMessage();
                } catch (Exception ex) {
                    Throwable th = ex.getCause() != null ? ex.getCause() : ex;
                    error = th.getMessage();
                    return false;
                }
                //5. Система проверяет, что можно продолжить работу
                if (!canProcess()) {
                    return true;
                }
                //6. Система проверяет, что пришло время записи в журнал
                transactions.executeWithoutResult(s -> plannersService.updateMonitoringDate(plannerLog.getId(), getPlanner()));
            }
        } finally {
            //6. Система записывает в PLANNERS_LOG
            finalizePlanner(plannerLog.getId(), error);
            //Сбрасываем статус необработанных сообщений
            resetUnfinishedMessages(inputs);
        }
    }

    @Override
    protected void afterWork() {}

    /**
     * Система проверяет, что выполняются следующие условия (через И):
     * processor.run.mode = 1;
     *
     * @return
     */
    @Override
    protected final boolean canProcess() {
        return Boolean.TRUE.equals(transactions.execute(status -> {
            boolean runMode = Boolean.TRUE.equals(settingService.getSettingProperty(getPlanner().getPlannerName() + ".run.mode", Boolean.class, false));
            return runMode;
        }));
    }

    private void resetUnfinishedMessages(List<EsuInput> inputs) {
        if (inputs == null || inputs.isEmpty()) {
            return;
        }
        transactions.executeWithoutResult(status ->
                inputs.forEach(m -> {
                    if (Objects.equals(EsuStatusType.IN_PROGRESS, m.getStatus())) {
                        m.setStatus(EsuStatusType.NEW);
                        esuInputCRUDRepository.save(m);
                    }
                })
        );
    }
}
