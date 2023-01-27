package moscow.ptnl.app.ecppis.deserilizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import moscow.ptnl.app.ecppis.model.Attribute;
import moscow.ptnl.app.ecppis.model.EntityData;
import moscow.ptnl.app.ecppis.model.ErpChangePatientPolicies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jayway.jsonpath.Configuration;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.jayway.jsonpath.Option;

@Component
public class ErpChangePatientPoliciesDeserializer implements Function<String, ErpChangePatientPolicies> {

    private Configuration configuration;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    public void setup() {
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
        configuration = Configuration.defaultConfiguration()
                .addOptions(Option.SUPPRESS_EXCEPTIONS);
    }

    @Override
    public ErpChangePatientPolicies apply(String json) {
        Object value = configuration.jsonProvider().parse(json);
        TypeRef<List<EntityData>> typeRef = new TypeRef<List<EntityData>>() {};

        return new ErpChangePatientPolicies(
                extractSingle(value, "$.id", Integer.class),
                extractSingle(value, "$.operationDate", LocalDateTime.class),
                extractSingle(value, "$.emiasId", Integer.class),
                JsonPath.read(value, "$.uklErp"),
                JsonPath.read(value, "$.patientType"),
                JsonPath.read(value, "$.patientRecStatus"),
                JsonPath.read(value, "$.entityName"),
                JsonPath.parse(json).read("$.entityData", typeRef)
        );

    }

    private <T> T extractSingleArray(Object value, String path, Class<T> clazz) {
        List<T> array = JsonPath.read(value, path);

        if (array.isEmpty()) {
            return null;
        }
        return mapper.convertValue(array.get(0), clazz);
    }

    private <T> List<T> extractList(Object value, String path, Class<T> clazz) {
        List<T> array = JsonPath.read(value, path);

        if (array.isEmpty()) {
            return null;
        }
        return array;
    }


    private <T> T extractSingle(Object value, String path, Class<T> clazz) {
        T array = JsonPath.read(value, path);

        if (array == null) {
            return null;
        }
        return mapper.convertValue(array, clazz);
    }
}
