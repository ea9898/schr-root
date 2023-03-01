package moscow.ptnl.schr.repository;

import moscow.ptnl.app.repository.BaseCrudRepository;
import moscow.ptnl.domain.entity.Setting;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface SettingsCRUDRepository extends BaseCrudRepository<Setting, String> {
}
