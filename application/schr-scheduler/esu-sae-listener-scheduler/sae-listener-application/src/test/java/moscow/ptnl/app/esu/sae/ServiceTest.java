package moscow.ptnl.app.esu.sae;

import moscow.ptnl.app.esu.sae.listener.config.RestConfiguration;
import moscow.ptnl.app.esu.sae.listener.processor.SchoolAttachmentEventProcessor;
import moscow.ptnl.app.esu.sae.listener.validator.ErpChangePatientSchoolBaseValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mos.emias.esu.lib.consumer.message.EsuMessage;
import ru.mos.emias.esu.lib.exception.EsuConsumerDoNotRetryException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author sevgeniy
 */
@SpringBootTest(classes = {
        PersistenceConfiguration.class,
        MockConfiguration.class,
        ErpChangePatientSchoolBaseValidator.class,
        SchoolAttachmentEventProcessor.class,
        RestConfiguration.class
})
public class ServiceTest {

    @Autowired
    private ErpChangePatientSchoolBaseValidator validator;

    @Autowired
    private SchoolAttachmentEventProcessor schoolAttachmentEventProcessor;

    @Test
    public void test() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/SchoolAttachmentEvent.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> errors = schoolAttachmentEventProcessor.validate(json);

        Assertions.assertTrue(errors.isEmpty());
    }

    @Test
    public void testWrong() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/SchoolAttachmentEventWrong.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> errors = schoolAttachmentEventProcessor.validate(json);

        Assertions.assertFalse(errors.isEmpty());
    }

}
