package moscow.ptnl.app.esu.sae.listener.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import moscow.ptnl.app.esu.sae.listener.model.erp.PatientSchoolAttachment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Component
public class PatientSchoolAttachmentDeserializer implements Function<String, PatientSchoolAttachment> {

    private Configuration configuration;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    public void setup() {
        configuration = Configuration.builder().jsonProvider(new JacksonJsonProvider(mapper))
                .build();
    }

    @Override
    public PatientSchoolAttachment apply(String json) {
        DocumentContext value = JsonPath.using(configuration).parse(json);
        LinkedList jsonArray = value.read("$.entityData[*]");
        int attachNum = jsonArray.size();

        return PatientSchoolAttachment.buildChecker(
                value.read("$.emiasId"),
                value.read("$.studentId"),
                value.read("$.studentPersonId"),
                mapper.convertValue(value.read("$.operationDate"), LocalDateTime.class),
                checkReqFieldsArray(value, "$.entityData[*].attributes[?(@.name==\"attachId\")].value.value", Long.class, attachNum),
                checkReqFieldsArray(value, "$.entityData[*].attributes[?(@.name==\"organizationId\")].value.value", Long.class, attachNum),
                checkReqFieldsArray(value, "$.entityData[*].attributes[?(@.name==\"areaId\")].value.value", Long.class, attachNum),
                checkReqFieldsArray(value, "$.entityData[*].attributes[?(@.name==\"attachStartDate\")].value.value", LocalDate.class, attachNum),
                checkReqFieldsArray(value, "$.entityData[*].attributes[?(@.name==\"classUid\")].value.value", String.class, attachNum),
                checkReqFieldsArray(value, "$.entityData[*].attributes[?(@.name==\"educationForm\")].value.id", Long.class, attachNum),
                checkReqFieldsArray(value, "$.entityData[*].attributes[?(@.name==\"educationForm\")].value.value", String.class, attachNum),
                checkReqFieldsArray(value, "$.entityData[*].attributes[?(@.name==\"trainingBeginDate\")].value.value", LocalDateTime.class, attachNum),
                checkReqFieldsArray(value, "$.entityData[*].attributes[?(@.name==\"trainingEndDate\")].value.value", LocalDateTime.class, attachNum),
                checkReqFieldsArray(value, "$.entityData[*].attributes[?(@.name==\"academicYear\")].value.id", Long.class, attachNum),
                checkReqFieldsArray(value, "$.entityData[*].attributes[?(@.name==\"academicYear\")].value.value", String.class, attachNum),
                checkReqFieldsArray(value, "$.entityData[*].attributes[?(@.name==\"isActual\")].value.value", Boolean.class, attachNum)
        );
    }


    public List<PatientSchoolAttachment> getPatientSchoolAttachments(String json) {
        DocumentContext value = JsonPath.using(configuration).parse(json);

        List<Object> entityDataList = mapper.convertValue(value.read("$.entityData"), List.class);
        List<PatientSchoolAttachment> attachmentDataList = new ArrayList<>();
        for (Object entityData : entityDataList) {
            PatientSchoolAttachment attachment = PatientSchoolAttachment.build(
                    JsonPath.read(value, "$.emiasId"),
                    extractSingleArray(entityData, "$.attributes[?(@.name==\"attachId\")].value.value", Long.class),
                    extractSingleArray(entityData, "$.attributes[?(@.name==\"organizationId\")].value.value", Long.class),
                    extractSingleArray(entityData, "$.attributes[?(@.name==\"areaId\")].value.value", Long.class),
                    extractSingleArray(entityData, "$.attributes[?(@.name==\"attachStartDate\")].value.value", LocalDate.class),
                    JsonPath.read(value, "$.studentId"),
                    JsonPath.read(value, "$.studentPersonId"),
                    extractSingleArray(entityData, "$.attributes[?(@.name==\"classUid\")].value.value", String.class),
                    extractSingleArray(entityData, "$.attributes[?(@.name==\"educationForm\")].value.id", Long.class),
                    extractSingleArray(entityData, "$.attributes[?(@.name==\"educationForm\")].value.value", String.class),
                    extractSingleArray(entityData, "$.attributes[?(@.name==\"trainingBeginDate\")].value.value", LocalDate.class),
                    extractSingleArray(entityData, "$.attributes[?(@.name==\"trainingEndDate\")].value.value", LocalDate.class),
                    extractSingleArray(entityData, "$.attributes[?(@.name==\"academicYear\")].value.id", Long.class),
                    extractSingleArray(entityData, "$.attributes[?(@.name==\"academicYear\")].value.value", String.class),
                    extractSingleArray(entityData, "$.attributes[?(@.name==\"isActual\")].value.value", Boolean.class),
                    mapper.convertValue(JsonPath.read(value, "$.operationDate"), LocalDateTime.class));
            attachmentDataList.add(attachment);
        }
        return attachmentDataList;
    }

    private <T> Boolean checkReqFieldsArray(DocumentContext value, String path, Class<T> clazz, int num) {
        List<T> array = value.read(path);

        if (array.isEmpty()) {
            return null;
        }
        return array.size() == num;
    }

    private <T> T extractSingleArray(Object value, String path, Class<T> clazz) {
        List<T> array = JsonPath.read(value, path);

        if (array.isEmpty()) {
            return null;
        }
        return mapper.convertValue(array.get(0), clazz);
    }

}
