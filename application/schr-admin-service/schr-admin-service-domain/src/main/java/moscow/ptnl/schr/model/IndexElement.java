package moscow.ptnl.schr.model;

public enum IndexElement {

    PATIENT_INFO("patientInfo"),
    ATTACHMENTS("attachments"),
    POLICY("policy"),
    STUD_INFO("studInfo"),
    CONSENTS_INFO("consentsInfo"),
    ANTHROPOMETRY_INFO("anthropometryInfo");

    private String element;

    IndexElement(String element) {
        this.element = element;
    }

    public String getElement() {
        return element;
    }

    public static IndexElement findIndexElement(String element) {
        switch (element) {
            case "patientInfo":
                return PATIENT_INFO;
            case "attachments":
                return ATTACHMENTS;
            case "policy":
                return POLICY;
            case "studInfo":
                return STUD_INFO;
            case "consentsInfo":
                return CONSENTS_INFO;
            case "anthropometryInfo":
                return ANTHROPOMETRY_INFO;
        }
        throw new RuntimeException("Неизвестный элемент индекса: " + element);
    }
}
