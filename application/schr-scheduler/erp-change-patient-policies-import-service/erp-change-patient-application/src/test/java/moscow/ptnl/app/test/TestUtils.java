package moscow.ptnl.app.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 *
 * @author m.kachalov
 */
public class TestUtils {
    
    private TestUtils(){}
    
    public static String readJson(String path) {
        InputStream inputStream = TestUtils.class.getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("invalid path: " + path);
        }
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }
    
}
