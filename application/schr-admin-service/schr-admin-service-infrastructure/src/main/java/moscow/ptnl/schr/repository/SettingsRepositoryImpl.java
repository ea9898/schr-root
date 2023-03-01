package moscow.ptnl.schr.repository;

import moscow.ptnl.app.repository.BaseRepository;
import moscow.ptnl.domain.entity.Setting;
import moscow.ptnl.domain.repository.SettingsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class SettingsRepositoryImpl extends BaseRepository implements SettingsRepository {

    @Autowired
    private SettingsCRUDRepository settingsCRUDRepository;

    @Override
    public Optional<Setting> findById(String propertyName, boolean refresh) {
        Optional<Setting> result = settingsCRUDRepository.findById(propertyName);
        if (refresh && result.isPresent()) {
            getEntityManager().refresh(result.get());
        }
        return result;
    }
    
    @Override
    public void update(Setting setting) {
        settingsCRUDRepository.save(setting);
    }
}
