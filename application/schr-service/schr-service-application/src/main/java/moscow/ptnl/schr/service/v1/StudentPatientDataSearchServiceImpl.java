package moscow.ptnl.schr.service.v1;

import moscow.ptnl.app.security.ExecutionContextHolder;
import moscow.ptnl.app.security.annotation.EMIASSecured;
import moscow.ptnl.schr.algorithm.JournalRequestHelper;
import moscow.ptnl.schr.dao.StudentPatientRegistryDAO;
import moscow.ptnl.schr.error.ErrorReason;
import moscow.ptnl.schr.error.ServiceException;
import moscow.ptnl.schr.model.DslQueryResult;
import moscow.ptnl.schr.service.StudentPatientDataSearchService;
import moscow.ptnl.schr.service.dto.SearchByDslRequest;
import moscow.ptnl.schr.service.dto.SearchByDslResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author m.kachalov
 */
@Service(StudentPatientDataSearchService.SERVICE_IMPL_V1)
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = true)
public class StudentPatientDataSearchServiceImpl implements StudentPatientDataSearchService {
    
    private final static Logger LOG = LoggerFactory.getLogger(StudentPatientDataSearchServiceImpl.class);
    
    @Autowired
    private StudentPatientRegistryDAO studentPatientRegistryDAO;
    
    @Autowired
    private JournalRequestHelper journalRequestHelper;
    
    @EMIASSecured(faultClass = ServiceException.class)
    @Override
    public SearchByDslResponse searchByDsl(SearchByDslRequest request) throws ServiceException{
        //1. Система выполняет проверку полномочий пользователя используя данные из системного параметра services.security.settings
        //сделано через аннотацию EMIASSecured               
        
        if (request.getDslQuery() == null) {
            throw new ServiceException(ErrorReason.BAD_REQUEST);
        }
        
        //2. Система выполняет GET запрос к хранилищу Elasticsearch 
        //3. Система осуществляет парсинг сообщения полученного на шаге 2
        DslQueryResult queryResult = studentPatientRegistryDAO.executeDslQuery(request.getDslQuery());
        
        if (queryResult.getReason() != null) {
            //Система осуществляет переход в АС.1
            switch (queryResult.getReason()) {
                case INTERNAL_SERVER_ERROR -> throw new ServiceException(ErrorReason.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера, индекс student_patient_registry_alias недоступен");
                case BAD_REQUEST -> throw new ServiceException(ErrorReason.BAD_REQUEST, queryResult.getErrorMessage());
            }            
        }
        
        //4. Система инициирует процесс асинхронного журналирования сведений о вызове метода A_SCHR_2 
        journalRequestHelper.addJournalRequestRecord(
            ExecutionContextHolder.getUserName(),
            ExecutionContextHolder.getSystemName(),
            ExecutionContextHolder.getUri(), 
            ExecutionContextHolder.getRequestBody(), 
            ErrorReason.SUCCESS.getCode()
        );
        
        //5. Сценарий завершен
        SearchByDslResponse response = new SearchByDslResponse();
        response.setTotalCount(queryResult.getTotalCount().intValue());
        response.getItems().addAll(queryResult.getItems());
        return response;
    }
    
}
