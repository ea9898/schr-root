package moscow.ptnl.app.esu.aei.listener.validator;

import com.google.common.collect.ImmutableList;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;
import org.everit.json.schema.FormatValidator;

public class DateTimeFormatValidator implements FormatValidator {
    private static final List<String> FORMATS_ACCEPTED = ImmutableList.of("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,9}");
    private static final String PARTIAL_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String ZONE_OFFSET_PATTERN = "XXX";
    private static final DateTimeFormatter FORMATTER;

    public DateTimeFormatValidator() {
    }

    public Optional<String> validate(String subject) {
        try {
            FORMATTER.parse(subject);
            return Optional.empty();
        } catch (DateTimeParseException var3) {
            return Optional.of(String.format("[%s] is not a valid date-time. Expected %s", subject, FORMATS_ACCEPTED));
        }
    }

    public String formatName() {
        return "date-time";
    }

    static {
        DateTimeFormatter secondsFractionFormatter = (new DateTimeFormatterBuilder()).appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true).toFormatter();
        DateTimeFormatterBuilder builder = (new DateTimeFormatterBuilder()).appendPattern("yyyy-MM-dd'T'HH:mm:ss").appendOptional(secondsFractionFormatter);
        FORMATTER = builder.toFormatter();
    }
}
