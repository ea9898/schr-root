package moscow.ptnl.schr.configuration;

import moscow.ptnl.app.rs.interceptor.RequestContextInterceptor;
import moscow.ptnl.schr.service.AdminService;
import moscow.ptnl.schr.transform.RESTExceptionMapper;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class RESTServiceConfiguration {

    @Autowired
    private Bus cxfBus;

    /**
     * http://localhost:8080/adminService?_wadl
     *
     * @param serviceImpl
     * @return
     */
    @Bean
    public Server createAdminService(@Qualifier(AdminService.SERVICE_IMPL) AdminService serviceImpl) {
        return createService(serviceImpl, "/adminService");
    }

    private Server createService(Object serviceImpl, String path) {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(cxfBus);
        endpoint.setServiceBean(serviceImpl);
        endpoint.setAddress(path);
        endpoint.setProviders(Arrays.asList(new JacksonJsonProvider(), new RESTExceptionMapper()));
        endpoint.setInInterceptors(Collections.singletonList(new RequestContextInterceptor()));
        return endpoint.create();
    }

}
