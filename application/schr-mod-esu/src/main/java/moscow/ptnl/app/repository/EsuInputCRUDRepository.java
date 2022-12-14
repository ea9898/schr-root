package moscow.ptnl.app.repository;

import moscow.ptnl.domain.entity.esu.EsuInput;
import moscow.ptnl.domain.entity.esu.EsuStatusType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface EsuInputCRUDRepository extends BaseCrudRepository<EsuInput, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "-2")})//-2 это LockOptions.SKIP_LOCKED
    @Query("SELECT e FROM EsuInput e WHERE (e.topic = :topic OR e.topic = :topicPrivate) AND (e.status = :statusWaiting OR e.status = :statusWorking AND e.updateDate < :stuckBeforeTime)")
    List<EsuInput> find(@Param("topic") String topic, @Param("topicPrivate") String topicPrivate, @Param("statusWaiting") EsuStatusType statusWaiting,
                        @Param("statusWorking") EsuStatusType statusWorking, @Param("stuckBeforeTime") LocalDateTime stuckBeforeTime,
                        Pageable pageable);
}
