package moscow.ptnl.schr.algorithm;

import java.time.LocalDateTime;
import java.util.UUID;
import moscow.ptnl.schr.dao.JournalRequestDAO;
import moscow.ptnl.app.domain.model.es.JournalRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author m.kachalov
 */
@Component
public class JournalRequestHelper {
    
    private final static Logger LOG = LoggerFactory.getLogger(JournalRequestHelper.class);
    
    @Autowired
    private JournalRequestDAO jroDao;
    
    /**
     * A_SCHR_2 Журналирование сведений о вызове метода.
     * 
     * @param userName
     * @param systemName
     * @param uri идентификатор ресурса, куда обращается клиент
     * @param restRequest текст запроса к методу
     * @param httpStatusCode код ошибки
     * @return ИД ошибки в журнале запросов
     */
    public UUID addJournalRequestRecord(String userName, String systemName, String uri, String restRequest, int httpStatusCode) {
        return addJournalRequestRecord(userName, systemName, uri, restRequest, httpStatusCode, null);
    }
    
    /**
     * A_SCHR_2 Журналирование сведений о вызове метода.
     * 
     * @param userName
     * @param systemName
     * @param uri идентификатор ресурса, куда обращается клиент
     * @param restRequest текст запроса к методу
     * @param httpStatusCode код ошибки
     * @param errorMessage текст ошибки
     * @return ИД ошибки в журнале запросов
     */
    public UUID addJournalRequestRecord(String userName, String systemName, String uri, String restRequest, int httpStatusCode, String errorMessage) {
        UUID id = UUID.randomUUID();
        
        JournalRequest object = new JournalRequest();
        object.setUuid(id.toString());
        object.setUserName(userName);
        object.setSystemName(systemName);
        object.setUri(uri);        
        object.setErrorMessage(errorMessage);
        object.setRestRequest(restRequest);
        object.setHttpStatusCode((short) httpStatusCode);
        object.setRequestDate(LocalDateTime.now());
        //A_SCHR_2 Журналирование сведений о вызове метода
        try {
            //метод работает асинхронно, ответа не ждем
            jroDao.write(object);
        } catch (Exception e) {
            LOG.error("Ошибка журналирования записи [" + id + "]", e);
        }
        return id;
    }
    
}
