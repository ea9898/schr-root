package moscow.ptnl.app.service;

import moscow.ptnl.domain.service.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sorlov
 */
@Service
public class TaskManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Autowired
    private SettingService settingService;

    /**
     * Считывает настройки из CONFIG для задачи и определяет нужно ли 
     * его включить/выключить.
     *
     * @param taskName
     * @return
     */
    public boolean checkConsumerIsOn(String taskName) {
        //настройка прослушивания конкретного топика
        boolean runMode = Boolean.TRUE.equals(settingService.getListenerRunMode(taskName));

        if (!runMode) {
            LOG.warn("Обработка топика {} отключена в настройках", taskName);
            return false; //если ничего не сказано, то и ничего не делаем - топик не слушаем
        }
        LocalTime[] workInterval = null;

        try {
            LocalTime begin = settingService.getListenerBeginDate(taskName);
            LocalTime end = settingService.getListenerEndDate(taskName);
            // получение рабочего интервала
            workInterval = new LocalTime[]{begin, end};
            LOG.debug("Обработка топика {} возможна в интервале от {} до {}", taskName, workInterval[0], workInterval[1]);
        } catch (Exception ex) {
            LOG.error("Ошибка получения параметров начала/окончания работы топика {}", taskName, ex);
        }

        //определение и возврат флага возможности потребления данных топика
        return checkIntervalContainsNow(workInterval);
    }

    /**
     * Проверяет, что текущее время попадает в интервал.
     *
     * @param interval
     * @return
     */
    public boolean checkIntervalContainsNow(LocalTime[] interval) {
        if (interval == null || interval.length < 2 || interval[0] == null || interval[1] == null) {
            return false;
        }
        List<LocalTime[]> intervalList = new ArrayList<>();
        if (interval[0].isAfter(interval[1]) || interval[0].equals(interval[1])) {
            intervalList.add(new LocalTime[]{LocalTime.of(0, 0), interval[1]});
            intervalList.add(new LocalTime[]{interval[0], LocalTime.of(23, 59, 59, 999999999)});
        } else {
            intervalList.add(interval);
        }
        for (LocalTime[] timeInterval : intervalList) {
            LocalDate nowDate = LocalDate.now();
            LocalDateTime[] todayStartInterval = new LocalDateTime[]{
                    LocalDateTime.of(nowDate, timeInterval[0]),
                    LocalDateTime.of(nowDate, timeInterval[1])};
            LocalDateTime nowDatetime = LocalDateTime.now();
            if (nowDatetime.isAfter(todayStartInterval[0]) && nowDatetime.isBefore(todayStartInterval[1])) {
                return true;
            }
        }
        return false;
    }
}
