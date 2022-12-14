package moscow.ptnl.app.service;

import moscow.ptnl.domain.entity.PlannersLog;
import moscow.ptnl.app.model.PlannersEnum;
import moscow.ptnl.app.repository.PlannerLogCRUDRepository;
import moscow.ptnl.app.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
public class PlannersServiceImpl implements moscow.ptnl.app.service.PlannersService {

    @Autowired
    private PlannerLogCRUDRepository plannerLogCRUDRepository;

    @Override
    public PlannersLog startPlanner(PlannersEnum plannerType) {
        PlannersLog plannerLogEntry = new PlannersLog();
        plannerLogEntry.setPlanner(plannerType.getPlannerName());
        plannerLogEntry.setStartDate(LocalDateTime.now());
        plannerLogEntry.setDateMonitoring(plannerLogEntry.getStartDate());
        plannerLogEntry.setHost(CommonUtils.getHostName());
        plannerLogEntry = plannerLogCRUDRepository.save(plannerLogEntry);
        return plannerLogEntry;
    }

    @Override
    public PlannersLog stopPlanner(Long plannerLogId, String error) {
        PlannersLog plannerLog = plannerLogCRUDRepository.findById(plannerLogId).orElseThrow(() ->
                new IllegalStateException("Не найдена запись в таблице PLANNERS_LOG"));
        plannerLog.setError(error);
        plannerLog.setEndDate(LocalDateTime.now());
        return plannerLog;
    }

    @Override
    public PlannersLog createOrGet(Long plannerLogId, PlannersEnum plannerType) {
        PlannersLog plannerLog = plannerLogCRUDRepository.findById(plannerLogId).orElse(null);

        if (plannerLog == null) {
            return startPlanner(plannerType);
        } else {
            return plannerLog;
        }
    }

    @Override
    public PlannersLog updateMonitoringDate(Long plannerLogId, PlannersEnum plannerType) {
        PlannersLog log = createOrGet(plannerLogId, plannerType);

        if (LocalDateTime.now().isAfter(log.getDateMonitoring().plusDays(1))) {
            //Обновляем дату для мониторинга работоспособности
            log.setDateMonitoring(LocalDateTime.now());
        }
        return log;
    }

    @Override
    public PlannersLog savePlannerError(PlannersEnum plannerType, String error) {
        PlannersLog plannerLogEntry = new PlannersLog();
        plannerLogEntry.setPlanner(plannerType.getPlannerName());
        plannerLogEntry.setStartDate(LocalDateTime.now());
        plannerLogEntry.setHost(CommonUtils.getHostName());
        plannerLogEntry.setError(error);
        plannerLogEntry.setEndDate(LocalDateTime.now());
        plannerLogEntry = plannerLogCRUDRepository.save(plannerLogEntry);
        return plannerLogEntry;
    }

    @Override
    public Long plannersLogClean() {
        throw new UnsupportedOperationException("Implement!");
//        return exemptionRepository.deleteOldPlannersLog(tablesCleanerSchedulerConfiguration.cleanerKeepLogs);
    }

}
