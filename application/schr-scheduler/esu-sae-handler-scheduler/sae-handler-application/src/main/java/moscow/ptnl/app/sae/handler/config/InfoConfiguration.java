package moscow.ptnl.app.sae.handler.config;

import javax.servlet.Servlet;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 *
 * @author m.kachalov
 */
@Configuration
public class InfoConfiguration {
    
    @Bean
    public ServletRegistrationBean<Servlet> infoServletRegistrationBean(WebApplicationContext webApplicationContext) {
        ServletRegistrationBean<Servlet> regBeen = new ServletRegistrationBean<>(new DispatcherServlet(webApplicationContext),
                "/info.json");
        regBeen.setLoadOnStartup(2);

        return regBeen;
    }
    
}
