package moscow.ptnl.app.esu.sae.listener.config;

import moscow.ptnl.app.config.PersistenceConstraint;
import moscow.ptnl.app.health.HealthCheckService;
import moscow.ptnl.app.health.HealthCheckServlet;
import moscow.ptnl.app.health.repository.DatabaseCheckRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.Servlet;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author m.kachalov
 */
@Configuration
@PropertySource("classpath:application.properties")
public class HealthConfiguration {
    
    @Value("${health.check.tables}")
    private String tables;
    
    @PersistenceContext(unitName = PersistenceConstraint.PU_NAME)
    protected EntityManager entityManager;
    
    @Bean      
    public ServletRegistrationBean<Servlet> healthServletRegistrationBean(HealthCheckService service) {
        
        List<String> tablesList = Arrays.asList(tables.split(","));
        ServletRegistrationBean<Servlet> regBeen = new ServletRegistrationBean<>(new HealthCheckServlet(service, tablesList),
                "/health");
        regBeen.setLoadOnStartup(3);

        return regBeen;
    }
    
    @Bean
    public DatabaseCheckRepository databaseCheckRepository(EntityManager em) {
        return new DatabaseCheckRepository() {
            @Override
            public EntityManager getEntityManager() {
                return em;
            }
        };
    }
}
