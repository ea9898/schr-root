package moscow.ptnl.app.health.repository;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Query;
import java.math.BigInteger;
import jakarta.persistence.EntityManager;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author sorlov
 */
@NoRepositoryBean
@Transactional(propagation = Propagation.MANDATORY)
public interface DatabaseCheckRepository {
    
    Integer TIMEOUT = 10000;
    
    EntityManager getEntityManager();    
    
    default long selectCountFromTable(String tableName, Integer timeout) {
        Query query = getEntityManager().createNativeQuery("SELECT COUNT(*) FROM " + tableName);
        query.setHint("jakarta.persistence.query.timeout", timeout);
        query.setHint("jakarta.persistence.lock.timeout", timeout);

        return (Long) query.getSingleResult();
    }
}
