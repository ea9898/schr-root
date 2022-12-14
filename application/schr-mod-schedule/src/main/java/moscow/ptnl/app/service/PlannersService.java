package moscow.ptnl.app.service;

import moscow.ptnl.domain.entity.PlannersLog;
import moscow.ptnl.app.model.PlannersEnum;

public interface PlannersService {

    PlannersLog startPlanner(PlannersEnum plannerType);

    PlannersLog stopPlanner(Long plannerLogId, String error);

    PlannersLog createOrGet(Long plannerLogId, PlannersEnum plannerType);

    PlannersLog updateMonitoringDate(Long plannerLogId, PlannersEnum plannerType);

    PlannersLog savePlannerError(PlannersEnum plannerType, String error);

    // М_ШОС_5 7.
    Long plannersLogClean();

}
