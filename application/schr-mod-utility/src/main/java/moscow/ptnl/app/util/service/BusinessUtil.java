package moscow.ptnl.app.util.service;

public class BusinessUtil {

    public static String convertFullNameToString(String firstName, String lastName, String patronymicName) {
        return (lastName == null ? "" : lastName + " ") +
                (firstName == null ? "" : firstName + " ") +
                (patronymicName == null ? "" : patronymicName).trim();
    }

}
