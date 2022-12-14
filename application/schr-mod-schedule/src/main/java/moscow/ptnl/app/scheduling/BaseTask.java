package moscow.ptnl.app.scheduling;

import moscow.ptnl.domain.entity.PlannersLog;
import moscow.ptnl.app.model.PlannersEnum;
import moscow.ptnl.app.service.PlannersService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

/**
 * @author sorlov
 */
public abstract class BaseTask {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    protected static final Integer TIMEOUT = 120000;

    @Autowired
    protected PlannersService plannersService;

    @Autowired
    protected TransactionTemplate transactions;

    protected LocalDateTime lastExecution;

    protected abstract boolean work(PlannersLog plannerLogEntry);

    protected abstract void afterWork();

    protected abstract PlannersEnum getPlanner();

    protected abstract boolean canProcess();

    public void runTask() {
        try {
            //1. С частотой, указанной в системном параметре taskAttachmentFormerService.startInterval,
            // планировщик проверяет возможность продолжения работы.
            // ЕСЛИ taskAttachmentFormerService.run.mode = true
            if (!canProcess()) {
                //Указанные условия выполняются, иначе завершение сценария
                return;
            }
            lastExecution = LocalDateTime.now();
            //2. Система записывает сведения о начале работы планировщика в PLANNERS_LOG
            PlannersLog plannerLogEntry = transactions.execute(status -> plannersService.startPlanner(getPlanner()));

            if (work(plannerLogEntry)) {
                //Успешное выполнение задачи
            } else {
            }
            lastExecution = LocalDateTime.now();
        } catch (Exception ex) {
            LOG.error("Unexpected error during task " + this.getClass().getSimpleName(), ex);
        } finally {
            try {
                afterWork();
            } catch (Exception ex) {
                LOG.error("Unexpected error during task shutdown " + this.getClass().getSimpleName(), ex);
            }
        }
    }

    protected void finalizePlanner(Long plannerLogId, String error){
        try {
            transactions.executeWithoutResult(s -> plannersService.stopPlanner(plannerLogId, error));
        } catch (Exception e) {
            LOG.error("Ошибка записи в PLANNERS_LOG", e);
        }
    }
}
