package moscow.ptnl.schr.configuration;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.soap.SOAPBinding;
import moscow.ptnl.schr.security.BusSecurityHeaderGenerator;
import moscow.ptnl.schr.security.UserContextHeaderGenerator;
import moscow.ptnl.schr.ws.interceptors.trace.TraceInterceptorService;
import net.ptnl.erp.school.v1.school_service.SchoolService;

import org.apache.cxf.binding.BindingConfiguration;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;
import java.util.Collections;

@Configuration
public class SchoolServiceConfiguration {

    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Autowired
    private TraceInterceptorService metricsInterceptorService;

    @Bean
    SchoolService schoolServicePortTypeBean(
            @Value("${emias.schoolservice.address}") String address,
            @Value("${security.sche.login}") String login) throws JAXBException, SOAPException {

        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(SchoolService.class);
        jaxWsProxyFactoryBean.setAddress(address);
        jaxWsProxyFactoryBean.setBindingConfig(new BindingConfiguration() {
            @Override
            public String getBindingId() {
                return SOAPBinding.SOAP12HTTP_BINDING;
            }
        });

        jaxWsProxyFactoryBean.getOutInterceptors().add(new BusSecurityHeaderGenerator(login));
        jaxWsProxyFactoryBean.getOutInterceptors().add(new UserContextHeaderGenerator(login, Collections.emptyList()));

        metricsInterceptorService.setupInterceptors(jaxWsProxyFactoryBean);

        SchoolService schoolService = (SchoolService) jaxWsProxyFactoryBean.create();
        ((BindingProvider) schoolService).getRequestContext().put("set-jaxb-validation-event-handler", Boolean.FALSE);

        return schoolService;
    }

}
