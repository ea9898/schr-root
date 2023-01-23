package moscow.ptnl.app.esu.ecppd.listener.deserializer;

import javax.annotation.PostConstruct;
import moscow.ptnl.app.esu.ecppd.listener.model.erp.AttachmentData;
import moscow.ptnl.app.esu.ecppd.listener.model.erp.SchoolAttachmentData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class SchoolAttachmentEventDeserializer implements Function<String, SchoolAttachmentData> {

    private Configuration configuration;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    public void setup() {
        configuration = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public SchoolAttachmentData apply(String json) {
        Object value = configuration.jsonProvider().parse(json);

        List<Object> entityDataList = mapper.convertValue(JsonPath.read(value, "$.entityData"), List.class);
        List<AttachmentData> attachmentDataList = new ArrayList<>();
        for (Object entityData : entityDataList) {
            AttachmentData attachmentData = new AttachmentData();
            attachmentData.setAttachId(extractSingleArray(entityData, "$.attributes[?(@.name==\"attachId\")].value.value", Long.class));
            attachmentData.setOrganizationId(extractSingleArray(entityData, "$.attributes[?(@.name==\"organizationId\")].value.value", Long.class));
            attachmentData.setAreaId(extractSingleArray(entityData, "$.attributes[?(@.name==\"areaId\")].value.value", Long.class));
            attachmentData.setAttachStartDate(extractSingleArray(entityData, "$.attributes[?(@.name==\"attachStartDate\")].value.value", LocalDate.class));
            attachmentData.setClassIdMesh(extractSingleArray(entityData, "$.attributes[?(@.name==\"classUid\")].value.value", String.class));
            attachmentData.setEducationFormId(extractSingleArray(entityData, "$.attributes[?(@.name==\"educationForm\")].value.id", Long.class));
            attachmentData.setEducationFormName(extractSingleArray(entityData, "$.attributes[?(@.name==\"educationForm\")].value.value", String.class));
            attachmentData.setTrainingBeginDate(extractSingleArray(entityData, "$.attributes[?(@.name==\"trainingBeginDate\")].value.value", LocalDate.class));
            attachmentData.setTrainingEndDate(extractSingleArray(entityData, "$.attributes[?(@.name==\"trainingEndDate\")].value.value", LocalDate.class));
            attachmentData.setAcademicYearId(extractSingleArray(entityData, "$.attributes[?(@.name==\"academicYear\")].value.id", Long.class));
            attachmentData.setAcademicYearName(extractSingleArray(entityData, "$.attributes[?(@.name==\"academicYear\")].value.value", String.class));
            attachmentData.setActual(extractSingleArray(entityData, "$.attributes[?(@.name==\"isActual\")].value.value", Boolean.class));
            attachmentDataList.add(attachmentData);
        }

        return SchoolAttachmentData.build(
                mapper.convertValue(JsonPath.read(value, "$.emiasId"), Long.class),
                JsonPath.read(value, "$.studentId"),
                JsonPath.read(value, "$.studentPersonId"),
                mapper.convertValue(JsonPath.read(value, "$.operationDate"), LocalDateTime.class),
                attachmentDataList
        );
    }

    private <T> T extractSingleArray(Object value, String path, Class<T> clazz) {
        List<T> array = JsonPath.read(value, path);
        if (array.isEmpty()) {
            return null;
        }
        return mapper.convertValue(array.get(0), clazz);
    }
}



