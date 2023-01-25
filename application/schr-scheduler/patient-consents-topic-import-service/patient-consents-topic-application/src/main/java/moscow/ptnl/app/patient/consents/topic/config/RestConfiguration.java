package moscow.ptnl.app.patient.consents.topic.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author sorlov
 */
@Configuration
public class RestConfiguration {

    @Bean("objectMapperOffsetTime")
    @Primary
    public ObjectMapper buildObjectMapper() {
        JavaTimeModule dateTimeModule = new JavaTimeModule();
        dateTimeModule.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        dateTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE));
        dateTimeModule.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        dateTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS);
        mapper.registerModule(dateTimeModule);
        mapper.enable(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setTimeZone(TimeZone.getDefault());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }

    @Bean("objectMapperLocalTime")
    public ObjectMapper buildObjectMapperLocal() {
        JavaTimeModule dateTimeModule = new JavaTimeModule();
        dateTimeModule.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        dateTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE));
        dateTimeModule.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        dateTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS);
        mapper.registerModule(dateTimeModule);
        mapper.enable(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setTimeZone(TimeZone.getDefault());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }
}
