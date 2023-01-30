package moscow.ptnl.schr.dao;

import java.util.concurrent.CompletableFuture;
import moscow.ptnl.app.domain.model.es.JournalRequest;
import moscow.ptnl.app.infrastructure.repository.es.JournalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author m.kachalov
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class JournalRequestDAO {
    
    @Autowired
    private JournalRequestRepository repository;
    
    @Async("asyncExecutor")
    public CompletableFuture<JournalRequest> write(JournalRequest object) {
        return CompletableFuture.completedFuture(repository.save(object));
    }
    
}
