package moscow.ptnl.app.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author m.kachalov
 */
@SpringBootApplication(scanBasePackages = {
    "moscow.ptnl.emiascej",
    "moscow.ptnl.app"
})
public class TestApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
    
}
