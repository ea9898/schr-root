package deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import model.ConsentDetails;
import model.DocumentedConsent;
import model.Immunodiagnostics;
import model.InterventionDetails;
import model.PatientConsents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Component
public class PatientConsentsTopicDeserializer implements Function<String, PatientConsents> {

    private Configuration configuration;

    @Autowired
    @Qualifier("objectMapperLocalTime")
    private ObjectMapper mapper;

    @PostConstruct
    public void setup() {
        configuration = Configuration.defaultConfiguration()
                .addOptions(Option.SUPPRESS_EXCEPTIONS);
    }

    @Override
    public PatientConsents apply(String json) {
        DocumentContext value = JsonPath.using(configuration).parse(json);

        Immunodiagnostics immunodiagnostics = extractSingle(value, "$.consentDetails.documentedConsent.Immunodiagnostics", Immunodiagnostics.class);

        ConsentDetails consentDetails = new ConsentDetails(
                extractSingle(value, "$.consentDetails.consentId", Long.class),
                new DocumentedConsent(
                        value.read("$.consentDetails.documentedConsent.documentId"),
                        extractSingle(value, "$.consentDetails.documentedConsent.createDate", LocalDate.class),
                        extractSingle(value, "$.consentDetails.documentedConsent.locationId", Long.class),
                        value.read("$.consentDetails.documentedConsent.locationName"),
                        value.read("$.consentDetails.documentedConsent.allMedicalIntervention"),
                        new InterventionDetails(
                                extractList(value, "$.consentDetails.documentedConsent.interventionDetails.medInterventionId[*]", Long.class)
                        ),
                        immunodiagnostics,
                        value.read("$.consentDetails.documentedConsent.representativeDocumentId"),
                        value.read("$.consentDetails.documentedConsent.signedByPatient"),
                        extractSingle(value, "$.consentDetails.documentedConsent.cancelReasonId", Long.class),
                        value.read("$.consentDetails.documentedConsent.cancelReasonOther"),
                        extractSingle(value, "$.consentDetails.documentedConsent.moId", Long.class),
                        value.read("$.consentDetails.documentedConsent.moName")
                ),
                extractSingle(value, "$.consentDetails.issueDateTime", LocalDateTime.class),
                extractSingle(value, "$.consentDetails.consentFormId", Long.class),
                extractSingle(value, "$.consentDetails.consentTypeId", Long.class),
                extractSingle(value, "$.consentDetails.representativePhysicalId", Long.class),
                extractSingle(value, "$.consentDetails.representativeLegalId", Long.class)
        );
        return new PatientConsents(extractSingle(value, "$.patientId", Long.class), consentDetails);
    }

    private <T> List<T> extractList(DocumentContext value, String path, Class<T> clazz) {
        List<T> array = value.read(path);

        if (array.isEmpty()) {
            return null;
        }
        return array;
    }

    private <T> T extractSingle(DocumentContext value, String path, Class<T> clazz) {
        T array = value.read(path);

        if (array == null) {
            return null;
        }
        return mapper.convertValue(array, clazz);
    }
}
