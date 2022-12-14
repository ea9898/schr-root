package moscow.ptnl.domain.repository;

import moscow.ptnl.domain.entity.Setting;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface SettingsRepository {

    Optional<Setting> findById(String propertyName, boolean refresh);
    
    void update(Setting setting);

}
