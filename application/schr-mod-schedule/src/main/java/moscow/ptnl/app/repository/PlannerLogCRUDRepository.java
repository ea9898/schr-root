package moscow.ptnl.app.repository;

import moscow.ptnl.domain.entity.PlannersLog;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface PlannerLogCRUDRepository extends BaseCrudRepository<PlannersLog, Long> {

    List<PlannersLog> findAllByStartDateLessThan(LocalDateTime dateStart);
}
