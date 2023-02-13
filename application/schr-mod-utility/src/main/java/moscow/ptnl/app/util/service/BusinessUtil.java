package moscow.ptnl.app.util.service;

import javax.xml.datatype.XMLGregorianCalendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

public class BusinessUtil {

    public static String convertFullNameToString(String firstName, String lastName, String patronymicName) {
        return (lastName == null ? "" : lastName + " ") +
                (firstName == null ? "" : firstName + " ") +
                (patronymicName == null ? "" : patronymicName).trim();
    }

    public static LocalDate convertGregorianToLocalDate(XMLGregorianCalendar data) {
        if (Objects.isNull(data)) {
            return null;
        }
        return LocalDate.of(data.getYear(), data.getMonth(), data.getDay());
    }

    public static LocalDateTime convertGregorianToLocalDateTime(XMLGregorianCalendar data) {
        if (Objects.isNull(data)) {
            return null;
        }
        Date utilDate = data.toGregorianCalendar().getTime();
        return LocalDateTime.ofInstant(utilDate.toInstant(), ZoneId.systemDefault());
    }
}
