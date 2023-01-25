package moscow.ptnl.app.esu.sae.listener.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import moscow.ptnl.app.esu.sae.listener.model.erp.PatientSchoolAttachment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class PatientSchoolAttachmentDeserializer implements Function<String, PatientSchoolAttachment> {

    private Configuration configuration;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    public void setup() {
        configuration = Configuration.defaultConfiguration()
                .addOptions(Option.SUPPRESS_EXCEPTIONS);
    }

    @Override
    public PatientSchoolAttachment apply(String json) {
        Object value = configuration.jsonProvider().parse(json);
        return getPatientSchoolAttachment(value);
    }

    public List<PatientSchoolAttachment> getPatientSchoolAttachments(String json) {
        Object value = configuration.jsonProvider().parse(json);

        List<Object> entityDataList = mapper.convertValue(JsonPath.read(value, "$.entityData"), List.class);
        List<PatientSchoolAttachment> attachmentDataList = new ArrayList<>();
        for (Object entityData : entityDataList) {
            PatientSchoolAttachment attachment = getPatientSchoolAttachment(entityData);
            attachmentDataList.add(attachment);
        }
        return attachmentDataList;
    }

    private PatientSchoolAttachment getPatientSchoolAttachment(Object value) {
        return PatientSchoolAttachment.build(JsonPath.read(value, "$.emiasId"),
                extractSingleArray(value, "$.entityData[*].attributes[?(@.name==\"attachId\")].value.value", Long.class),
                extractSingleArray(value, "$.entityData[*].attributes[?(@.name==\"organizationId\")].value.value", Long.class),
                extractSingleArray(value, "$.attributes[?(@.name==\"areaId\")].value.value", Long.class),
                extractSingleArray(value, "$.attributes[?(@.name==\"attachStartDate\")].value.value", LocalDate.class),
                JsonPath.read(value, "$.studentId"),
                JsonPath.read(value, "$.studentPersonId"),
                extractSingleArray(value, "$.entityData[*].attributes[?(@.name==\"classUid\")].value.value", String.class),
                extractSingleArray(value, "$.attributes[?(@.name==\"educationForm\")].value.id", Long.class),
                extractSingleArray(value, "$.attributes[?(@.name==\"educationForm\")].value.value", String.class),
                extractSingleArray(value, "$.attributes[?(@.name==\"trainingBeginDate\")].value.value", LocalDate.class),
                extractSingleArray(value, "$.attributes[?(@.name==\"trainingEndDate\")].value.value", LocalDate.class),
                extractSingleArray(value, "$.entityData[*].attributes[?(@.name==\"academicYear\")].value.id", Long.class),
                extractSingleArray(value, "$.entityData[*].attributes[?(@.name==\"academicYear\")].value.value", String.class),
                extractSingleArray(value, "$.entityData[*].attributes[?(@.name==\"isActual\")].value.value", Boolean.class),
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
