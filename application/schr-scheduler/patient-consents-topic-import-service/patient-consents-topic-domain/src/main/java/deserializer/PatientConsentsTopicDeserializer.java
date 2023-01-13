package deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import model.ConsentDetails;
import model.DocumentedConsent;
import model.Immunodiagnostics;
import model.InterventionDetails;
import model.PatientConsents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Component
public class PatientConsentsTopicDeserializer implements Function<String, PatientConsents> {

    private Configuration configuration;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    public void setup() {
        configuration = Configuration.defaultConfiguration()
                .addOptions(Option.SUPPRESS_EXCEPTIONS);
    }

    @Override
    public PatientConsents apply(String json) {
        Object value = configuration.jsonProvider().parse(json);

        Immunodiagnostics immunodiagnostics = JsonPath.parse(json).read("$.consentDetails.documentedConsent.immunodiagnostics", Immunodiagnostics.class);

        ConsentDetails consentDetails = new ConsentDetails(
                extractSingle(value, "$.consentDetails.consentId", Long.class),
                new DocumentedConsent(
                        JsonPath.read(value, "$.consentDetails.documentedConsent.documentId"),
                        extractSingle(value, "$.consentDetails.documentedConsent.createDate", LocalDate.class),
                        extractSingle(value, "$.consentDetails.documentedConsent.locationId", Long.class),
                        JsonPath.read(value, "$.consentDetails.documentedConsent.locationName"),
                        JsonPath.read(value, "$.consentDetails.documentedConsent.allMedicalIntervention"),
                        new InterventionDetails(
                                extractList(value, "$.consentDetails.documentedConsent.interventionDetails.medInterventionId[*]", Integer.class)
                        ),
                        immunodiagnostics,
                        JsonPath.read(value, "$.consentDetails.documentedConsent.representativeDocumentId"),
                        extractSingle(value, "$.consentDetails.documentedConsent.templateId", Long.class),
                        JsonPath.read(value, "$.consentDetails.documentedConsent.signedByPatient"),
                        extractSingle(value, "$.consentDetails.documentedConsent.cancelReasonId", Long.class),
                        JsonPath.read(value, "$.consentDetails.documentedConsent.cancelReasonOther"),
                        extractSingle(value, "$.consentDetails.documentedConsent.moId", Long.class),
                        JsonPath.read(value, "$.consentDetails.documentedConsent.moName")
                ),
                extractSingle(value, "$.consentDetails.issueDate", LocalDate.class),
                extractSingle(value, "$.consentDetails.consentFormId", Long.class),
                extractSingle(value, "$.consentDetails.consentTypeId", Long.class),
                extractSingle(value, "$.consentDetails.representativePhysicalId", Long.class),
                extractSingle(value, "$.consentDetails.representativeLegalId", Long.class)
        );
        return new PatientConsents(consentDetails);

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
