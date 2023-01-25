package moscow.ptnl.app.esu.pct.handler.configuration;

import moscow.ptnl.app.config.PersistenceConstraint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;
import java.util.ResourceBundle;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {
                "moscow.ptnl"
        },
        entityManagerFactoryRef = PersistenceConstraint.MF_BEAN_NAME,
        transactionManagerRef = PersistenceConstraint.TM_BEAN_NAME
)
@PropertySource("classpath:persistence.properties")
public class PersistenceConfiguration {

    @Bean(name = PersistenceConstraint.DS_BEAN_NAME)
    public DataSource dataSource() {
        ResourceBundle bundle = ResourceBundle.getBundle("persistence");
        DriverManagerDataSource dataSource = null;
        dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(bundle.getString("jdbc.driverClassName"));
        dataSource.setUrl(bundle.getString("jdbc.url"));
        return dataSource;
    }

    @Bean(name = PersistenceConstraint.MF_BEAN_NAME)
    public EntityManagerFactory entityManagerFactory(@Qualifier(PersistenceConstraint.DS_BEAN_NAME) DataSource dataSource) {
        ResourceBundle bundle = ResourceBundle.getBundle("persistence");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(false);
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("hibernate.jdbc.fetch_size", bundle.getString("org.hibernate.fetchSize"));
        jpaProperties.setProperty("hibernate.enable_lazy_load_no_trans", "true");
        factory.setJpaProperties(jpaProperties);
        factory.setPackagesToScan("moscow.ptnl");
        factory.setDataSource(dataSource);
        factory.setPersistenceUnitName(PersistenceConstraint.PU_NAME);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean(name = PersistenceConstraint.TM_BEAN_NAME)
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier(PersistenceConstraint.MF_BEAN_NAME) EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

    @Bean
    @ConditionalOnMissingBean({TransactionOperations.class})
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
