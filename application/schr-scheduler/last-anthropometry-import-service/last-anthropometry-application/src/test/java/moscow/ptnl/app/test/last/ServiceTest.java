package moscow.ptnl.app.test.last;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import moscow.ptnl.app.last.anthropometry.validator.LastAnthropometryValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mos.emias.esu.model.LastAnthropometry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sevgeniy
 */
@SpringBootTest(classes = {
        LastAnthropometryValidator.class,
        ObjectMapper.class
})
public class ServiceTest {

    @Autowired
    private LastAnthropometryValidator validator;

    @Autowired
    protected ObjectMapper mapper;

    @Test
    public void lastAnthropometry() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isEmpty());
    }

    @Test
    public void lastAnthropometryPatientIdIncorrect() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-id-incorrect.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/patientId: expected type: String, found: Long", validate.get());
    }

    @Test
    public void lastAnthropometryPatientIdIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-id-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/patientId: expected type: String, found: Null", validate.get());
    }

    @Test
    public void lastAnthropometryMeasurementDateIncorrect() throws JsonProcessingException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-measurement-date-incorrect.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        int index = Integer.parseInt(validate.get().substring(60, 61));

        mapper.registerModule(new JavaTimeModule());
        LastAnthropometry readValue = mapper.readValue(json, LastAnthropometry.class);

        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/" + index + "/measurementDate: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void lastAnthropometryMeasurementDateNullList() throws JsonProcessingException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-measurement-date-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);

        LastAnthropometry readValue = mapper.readValue(json, LastAnthropometry.class);

        Assertions.assertTrue(validate.isPresent());
        for (int i = 0; i < readValue.getMeasurement().size(); i++) {
            if (readValue.getMeasurement().get(i).getMeasurementDate() == null) {
                Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/" + i + "/measurementDate: expected type: String, found: Null", validate.get());
            }
        }
    }

    @Test
    public void lastAnthropometryMeasurementDateNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-measurement-date-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);

        int index = Integer.parseInt(validate.get().substring(60, 61));

        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/" + index + ": required key [measurementDate] not found", validate.get());
    }

    @Test
    public void lastAnthropometryDocumentIdIncorrect() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-document-id-incorrect.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);

        int index = Integer.parseInt(validate.get().substring(60, 61));

        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/" + index + "/documentId: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void lastAnthropometryDocumentIdIsNullList() throws JsonProcessingException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-document-id-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);

        mapper.registerModule(new JavaTimeModule());
        LastAnthropometry readValue = mapper.readValue(json, LastAnthropometry.class);

        Assertions.assertTrue(validate.isPresent());
        for (int i = 0; i < readValue.getMeasurement().size(); i++) {
            if (readValue.getMeasurement().get(i).getDocumentId() == null) {
                Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/" + i + "/documentId: expected type: String, found: Null", validate.get());
            }
        }
    }

    @Test
    public void lastAnthropometryDocumentIdNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-document-id-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);

        int index = Integer.parseInt(validate.get().substring(60, 61));

        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/" + index + ": required key [documentId] not found", validate.get());
    }

    @Test
    public void lastAnthropometryMeasurementTypeIncorrect() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-measurement-type-incorrect.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);

        int index = Integer.parseInt(validate.get().substring(60, 61));

        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/" + index + "/measurementType: expected type: Number, found: String", validate.get());
    }

    @Test
    public void lastAnthropometryMeasurementTypeIsNullList() throws JsonProcessingException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-measurement-type-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);

        LastAnthropometry readValue = mapper.readValue(json, LastAnthropometry.class);

        Assertions.assertTrue(validate.isPresent());
        for (int i = 0; i < readValue.getMeasurement().size(); i++) {
            if (readValue.getMeasurement().get(i).getMeasurementType() == null) {
                Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/" + i + "/measurementType: expected type: Number, found: Null", validate.get());
            }
        }
    }

    @Test
    public void lastAnthropometryMeasurementTypeNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-measurement-type-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);

        int index = Integer.parseInt(validate.get().substring(60, 61));

        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/" + index + ": required key [measurementType] not found", validate.get());
    }

    @Test
    public void lastAnthropometryMeasurementValueIncorrect() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-measurement-value-incorrect.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);

        int index = Integer.parseInt(validate.get().substring(60, 61));

        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/" + index + "/measurementValue: expected type: Number, found: String", validate.get());
    }

    @Test
    public void lastAnthropometryMeasurementValueIsNullList() throws JsonProcessingException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-measurement-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);

        LastAnthropometry readValue = mapper.readValue(json, LastAnthropometry.class);

        Assertions.assertTrue(validate.isPresent());
        for (int i = 0; i < readValue.getMeasurement().size(); i++) {
            if (readValue.getMeasurement().get(i).getMeasurementValue() == null) {
                Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/" + i + "/measurementValue: expected type: Number, found: Null", validate.get());
            }
        }
    }

    @Test
    public void lastAnthropometryMeasurementValueNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-measurement-value-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);

        int index = Integer.parseInt(validate.get().substring(60, 61));

        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/" + index + ": required key [measurementValue] not found", validate.get());
    }

    @Test
    public void lastAnthropometryCentilityIncorrect() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-centility-incorrect.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);

        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/0/centility: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void lastAnthropometryCentilityIsNullList() throws JsonProcessingException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/last-anthropometry-centility-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);

        mapper.registerModule(new JavaTimeModule());
        LastAnthropometry readValue = mapper.readValue(json, LastAnthropometry.class);

        Assertions.assertTrue(validate.isPresent());
        for (int i = 0; i < readValue.getMeasurement().size(); i++) {
            if (readValue.getMeasurement().get(i).getCentility() == null) {
                Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/measurement/" + i + "/centility: expected type: String, found: Null", validate.get());
            }
        }
    }
}

