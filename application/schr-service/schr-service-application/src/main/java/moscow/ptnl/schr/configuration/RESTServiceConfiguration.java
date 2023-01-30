package moscow.ptnl.schr.configuration;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import java.util.Arrays;
import moscow.ptnl.app.rs.interceptor.RequestContextInterceptor;
import moscow.ptnl.schr.algorithm.JournalRequestHelper;
import moscow.ptnl.schr.service.StudentPatientDataSearchService;
import moscow.ptnl.schr.transform.RESTExceptionMapper;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author m.kachalov
 */
@Configuration
public class RESTServiceConfiguration {
    
    @Autowired
    private Bus cxfBus;
    
    /**
     * http://localhost:8080/studentPatientDataSearchService/v1?_wadl
     * @param serviceImpl
     * @param journalRequestHelper
     * @return 
     */
    @Bean
    public Server createStudentPatientDataSearchServiceV1(
            @Qualifier(StudentPatientDataSearchService.SERVICE_IMPL_V1) StudentPatientDataSearchService serviceImpl,
            JournalRequestHelper journalRequestHelper) {       
        return createService(serviceImpl, "/studentPatientDataSearchService/v1", journalRequestHelper);
    }
    
    private Server createService(Object serviceImpl, String path, JournalRequestHelper journalRequestHelper) {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(cxfBus);
        endpoint.setServiceBean(serviceImpl);
        endpoint.setAddress(path);
        endpoint.setProviders(
            Arrays.asList(
                new JacksonJsonProvider(),
                new RESTExceptionMapper(journalRequestHelper)
            )
        );
        endpoint.setInInterceptors(Arrays.asList(new RequestContextInterceptor()));
        return endpoint.create();
    }
    
}
