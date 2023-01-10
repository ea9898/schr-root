package moscow.ptnl.app.patient.consents.topic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author sevgeniy
 */
@SpringBootApplication(scanBasePackages = {
        "moscow.ptnl.app",
        "moscow.ptnl.domain",
        "moscow.ptnl.schr.repository",
        "deserializer"
})
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
}
