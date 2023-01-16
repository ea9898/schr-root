package moscow.ptnl.app.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;
import java.util.ResourceBundle;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.support.Repositories;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {
                "moscow.ptnl.app", "moscow.ptnl.schr"
        },
        entityManagerFactoryRef = PersistenceConstraint.MF_BEAN_NAME,
        transactionManagerRef = PersistenceConstraint.TM_BEAN_NAME
)
@PropertySources({
        @PropertySource("classpath:persistence.properties")
})
public class PersistenceConfiguration {

    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private Integer maxPoolSize;

    @Value("${spring.datasource.hikari.maxIdleTime}")
    private Integer idleTimeout;

    @Value("${spring.datasource.hikari.maxTimeout}")
    private Integer maxLifetime;

    @Value("${spring.jpa.properties.hibernate.show-slow-sql}")
    private Boolean showSlowSql;

    @Bean(name = PersistenceConstraint.DS_BEAN_NAME)
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driver);
        hikariConfig.setJdbcUrl(datasourceUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(maxPoolSize);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setIdleTimeout(idleTimeout);
        hikariConfig.setMaxLifetime(maxLifetime);
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = PersistenceConstraint.TM_BEAN_NAME)
    @Primary
    public PlatformTransactionManager hibernateTransactionManager(@Qualifier(PersistenceConstraint.MF_BEAN_NAME) EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

    @Bean(name = PersistenceConstraint.MF_BEAN_NAME)
    public EntityManagerFactory entityManagerFactory(@Qualifier(PersistenceConstraint.DS_BEAN_NAME) DataSource dataSource) {
        ResourceBundle bundle = ResourceBundle.getBundle("persistence");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(showSlowSql);
        vendorAdapter.setGenerateDdl(false);
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        Properties jpaProperties = new Properties();
        final String prefix = "spring.jpa.properties.";
        bundle.keySet().stream()
                .filter(k -> k.startsWith(prefix))
                .map(k -> k.substring(prefix.length()))
                .forEach(k -> jpaProperties.setProperty(k, bundle.getString(prefix + k)));
        factory.setJpaProperties(jpaProperties);
        factory.setPackagesToScan("moscow.ptnl");
        factory.setDataSource(dataSource);
        factory.setPersistenceUnitName(PersistenceConstraint.PU_NAME);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
    
    @Bean @Lazy
    public Repositories repositories(ApplicationContext context) {
        return new Repositories(context);
    }

}
