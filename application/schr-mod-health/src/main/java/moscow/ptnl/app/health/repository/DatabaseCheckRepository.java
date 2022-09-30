package moscow.ptnl.app.health.repository;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.math.BigInteger;
import javax.persistence.EntityManager;
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
        query.setHint("javax.persistence.query.timeout", timeout);
        query.setHint("javax.persistence.lock.timeout", timeout);

        return ((BigInteger) query.getSingleResult()).longValue();
    }
}
