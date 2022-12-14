package moscow.ptnl.app.repository;

import moscow.ptnl.app.config.PersistenceConstraint;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class BaseRepository {

    @PersistenceContext(unitName = PersistenceConstraint.PU_NAME)
    protected EntityManager entityManager;
    
    public EntityManager getEntityManager() {
        return entityManager;
    }

}
