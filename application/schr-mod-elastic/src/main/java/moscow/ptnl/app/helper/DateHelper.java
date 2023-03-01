package moscow.ptnl.app.helper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateHelper {

    public static LocalDateTime convertToLocalDateTimeViaMilisecond(Date dateToConvert) {
        if (dateToConvert == null) return null;
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
