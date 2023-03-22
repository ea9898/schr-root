package moscow.ptnl.app.esu.ecppd.listener.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import moscow.ptnl.app.esu.ecppd.listener.model.erp.PatientPersonalData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Component
public class PatientPersonalDataDeserializer implements Function<String, PatientPersonalData> {

    private Configuration configuration;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    public void setup() {
        configuration = Configuration.defaultConfiguration()
                .addOptions(Option.SUPPRESS_EXCEPTIONS);
    }

    @Override
    public PatientPersonalData apply(String json) {
        Object value = configuration.jsonProvider().parse(json);

        return PatientPersonalData.build(mapper.convertValue(JsonPath.read(value, "$.emiasId"), Long.class),
                JsonPath.read(value, "$.uklErp"),
                extractSingleArray(value, "$.entityData[0].attributes[?(@.name==\"lastName\")].value.value", String.class),
                extractSingleArray(value, "$.entityData[0].attributes[?(@.name==\"firstName\")].value.value", String.class),
                extractSingleArray(value, "$.entityData[0].attributes[?(@.name==\"middleName\")].value.value", String.class),
                extractSingleArray(value, "$.entityData[0].attributes[?(@.name==\"birthDate\")].value.value", LocalDate.class),
                extractSingleArray(value, "$.entityData[0].attributes[?(@.name==\"gender\")].value.code", String.class),
                extractSingleArray(value, "$.entityData[0].attributes[?(@.name==\"deathDateTime\")].value.value", LocalDateTime.class),
                mapper.convertValue(JsonPath.read(value, "$.operationDate"), LocalDateTime.class));
    }

    private <T> T extractSingleArray(Object value, String path, Class<T> clazz) {
        List<T> array = JsonPath.read(value, path);

        if (array.isEmpty()) {
            return null;
        }
        return mapper.convertValue(array.get(0), clazz);
    }
}
