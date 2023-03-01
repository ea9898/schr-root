package moscow.ptnl.schr.servlet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * @author sorlov
 */
@RestController
public class InfoServlet {

    @GetMapping("/schr/info.json")
    public String info() {
        InputStream resource = getClass().getClassLoader().getResourceAsStream("info.json");
        return new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

    }
}
