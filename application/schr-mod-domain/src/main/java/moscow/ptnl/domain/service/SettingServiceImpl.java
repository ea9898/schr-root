package moscow.ptnl.domain.service;

import moscow.ptnl.domain.entity.Setting;
import moscow.ptnl.domain.repository.SettingsRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation=Propagation.REQUIRED)
public class SettingServiceImpl implements SettingService {
    
    private static final Logger LOG = LoggerFactory.getLogger(moscow.ptnl.domain.service.SettingServiceImpl.class);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String LIST_DELIMITER = ";";

    @Autowired
    private SettingsRepository settingsRepository;

    @Override
    public <T> T getSettingProperty(String propertyName, Class<T> type, boolean refresh) {
        Optional<Setting> settingOptional = settingsRepository.findById(propertyName, refresh);

        if (settingOptional.isPresent()) {
            Optional<T> value = getSettingValue(settingOptional.get(), type);
            if (value.isPresent()) {
                return value.get();
            }
        } else {
            LOG.warn("Не найдена настройка {}", propertyName);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getSettingListProperty(String propertyName, Class<T> type, boolean refresh) {
        Optional<Setting> settingOptional = settingsRepository.findById(propertyName, refresh);

        if (settingOptional.isPresent()) {
            List<String> values = Arrays.asList(getSettingValue(settingOptional.get(), String.class).orElse("")
                    .split(LIST_DELIMITER));

            if (type.equals(String.class)) {
                return (List<T>) values;
            }
            List<T> results = new ArrayList<>();
            values.forEach(v -> results.add(convertValue(v, type)));
            return results;
        } else {
            LOG.warn("Не найдена настройка {}", propertyName);
        }
        return null;
    }

    private <T> Optional<T> getSettingValue(Setting setting, Class<T> type) {
        T value = null;
        try {                
            value = convertValue(setting.getValue(), type);
        } catch (Exception ex) {
            LOG.error(String.format("Ошибка парсинга настройки %s1", setting.getCode()), ex);
        }
        return Optional.ofNullable(value);
    }
    
    @Override
    public void updateSettings(Setting ... settings) {
        for(Setting setting : settings) {
            settingsRepository.update(setting);
        }
    }

    @Override
    public Boolean getListenerRunMode(String topic) {
        return getSettingProperty(topic + ".run.mode", Boolean.class, false);
    }

    @Override
    public LocalTime getListenerBeginDate(String topic) {
        LocalTime begin = getSettingProperty(topic + ".run.begin-time", LocalTime.class, false);
        if (begin == null) {
            begin = LocalTime.MIN;
        }
        return begin;
    }

    @Override
    public LocalTime getListenerEndDate(String topic) {
        LocalTime end = getSettingProperty(topic + ".run.end-time", LocalTime.class, false);
        if (end == null) {
            end = LocalTime.MAX;
        }
        return end;
    }

    /**
     * Конвертирует строковое значение, приводя его к актуальному типу.
     *
     * @param <T>
     * @param value
     * @return null, Long, LocalDate, String, LocalTime
     */
    @SuppressWarnings("unchecked")
    private static <T> T convertValue(String value, Class<T> type) {
        if (value == null) {
            return null;
        }
        if (type.equals(Long.class)) {
            return (T) (Long) Long.parseLong(value);
        }
        else if (type.equals(Integer.class)) {
            return (T) (Integer) Integer.parseInt(value);
        }
        else if (type.equals(LocalDate.class)) {
            return (T) LocalDate.parse(value, DATE_FORMAT);
        }
        else if (type.equals(String.class)) {
            return (T) value;
        }
        else if (type.equals(LocalTime.class)) {
            return (T) LocalTime.parse(value, TIME_FORMAT);
        }
        else if (type.equals(Boolean.class)) {
            return (T) (Boolean) "1".equals(value);
        }
        throw new RuntimeException("Не поддерживаемый тип данных");
    }

    @Override
    public Long getReadTimeoutESU() {
        return getSettingProperty("esu.readTimeout", Long.class, false);
    }

    @Override
    public Long getEmptyQueueWaitTimeESU() {
        return getSettingProperty("esu.emptyQueueWaitTime", Long.class, false);
    }

}
