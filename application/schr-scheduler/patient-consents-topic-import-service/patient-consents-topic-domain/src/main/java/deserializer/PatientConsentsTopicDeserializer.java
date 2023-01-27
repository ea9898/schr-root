package deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
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
                .mappingProvider(new JacksonMappingProvider(mapper))
                .addOptions(Option.SUPPRESS_EXCEPTIONS);
    }

    @Override
    public PatientConsents apply(String json) {
        DocumentContext value = JsonPath.using(configuration).parse(json);

        Immunodiagnostics immunodiagnostics = value.read("$.message.consentDetails.documentedConsent.Immunodiagnostics", Immunodiagnostics.class);

        ConsentDetails consentDetails = new ConsentDetails(
                value.read("$.message.consentDetails.consentId", Long.class),
                new DocumentedConsent(
                        value.read("$.message.consentDetails.documentedConsent.documentId"),
                        value.read("$.message.consentDetails.documentedConsent.createDate", LocalDate.class),
                        value.read("$.message.consentDetails.documentedConsent.locationId", Long.class),
                        value.read("$.message.consentDetails.documentedConsent.locationName"),
                        value.read("$.message.consentDetails.documentedConsent.allMedicalIntervention"),
                        new InterventionDetails(
                                extractList(value, "$.message.consentDetails.documentedConsent.interventionDetails.medInterventionId[*]", Long.class)
                        ),
                        immunodiagnostics,
                        value.read("$.message.consentDetails.documentedConsent.representativeDocumentId"),
                        value.read("$.message.consentDetails.documentedConsent.signedByPatient"),
                        value.read("$.message.consentDetails.documentedConsent.cancelReasonId", Long.class),
                        value.read("$.message.consentDetails.documentedConsent.cancelReasonOther"),
                        value.read("$.message.consentDetails.documentedConsent.moId", Long.class),
                        value.read("$.message.consentDetails.documentedConsent.moName")
                ),
                value.read("$.message.consentDetails.issueDateTime", LocalDateTime.class),
                value.read("$.message.consentDetails.consentFormId", Long.class),
                value.read("$.message.consentDetails.consentTypeId", Long.class),
                value.read("$.message.consentDetails.representativePhysicalId", Long.class),
                value.read("$.message.consentDetails.representativeLegalId", Long.class)
        );
        return new PatientConsents(value.read("$.message.patientId", Long.class), consentDetails);
    }

    private <T> List<T> extractList(DocumentContext value, String path, Class<T> clazz) {
        List<T> array = value.read(path);

        if (array.isEmpty()) {
            return null;
        }
        return array;
    }
}
