package moscow.ptnl.domain.service;

import moscow.ptnl.domain.entity.Setting;

import java.time.LocalTime;
import java.util.List;

public interface SettingService {

    /** Настройки ограничения доступа к методам web-сервиса. */
    String SERVICES_SECURITY_SETTINGS = "services.security.settings";
    String MAX_PAGE_SIZE = "max_page_size";
    String DEFAULT_PAGE_NUMBER = "paging_default_page_number";
    String DEFAULT_PAGE_SIZE = "paging_default_page_size";
    /** PAR_1. Список разрешенных типов записей */
    String RECORD_TYPES = "recordTypes";

    <T> T getSettingProperty(String propertyName, Class<T> type, boolean refresh);

    <T> List<T> getSettingListProperty(String propertyName, Class<T> type, boolean refresh);

    void updateSettings(Setting ... settings);

    Boolean getListenerRunMode(String topic);

    LocalTime getListenerBeginDate(String topic);

    LocalTime getListenerEndDate(String topic);

    Long getReadTimeoutESU();

    Long getEmptyQueueWaitTimeESU();
}
