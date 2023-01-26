package moscow.ptnl.app.esu.sae;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author m.kachalov
 */
@SpringBootApplication(scanBasePackages = {
        "moscow.ptnl",
        "moscow.ptnl.app",
})
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
