package moscow.ptnl.app.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import moscow.ptnl.app.health.repository.DatabaseCheckRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sorlov
 */
@Service
public class HealthCheckService {
    
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckService.class);

    @Autowired
    private DatabaseCheckRepository databaseCheckRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRED)
    public Future<String> databaseHealthCheck(List<String> tablesList) {
        return new AsyncResult<>(tablesList.stream()
                .filter(t -> !t.startsWith("temp_") && !t.contains("_temp_"))
                .map(t -> {
                    try {
                        long startTime = System.currentTimeMillis();
                        long cnt = databaseCheckRepository.selectCountFromTable(t, DatabaseCheckRepository.TIMEOUT);
                        return "Fetch number of records " + cnt +
                            " from " + t + " in " + (System.currentTimeMillis() - startTime) + "ms";
                    } catch (Exception e) {
                        LOG.error("Ошибка выполнения запроса к таблице: " + t, e);
                        throw e;
                    }
                })
                .collect(Collectors.joining("\n"))
        );
    }
}
