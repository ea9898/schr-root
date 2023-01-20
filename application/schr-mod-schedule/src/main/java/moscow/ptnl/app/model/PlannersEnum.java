package moscow.ptnl.app.model;

public enum PlannersEnum {

    I_SCHR_1(1, "attachmentEventImportService"),
    I_SCHR_2(2, "attachmentEventHandlerService"),
    I_SCHR_3(3, "erpChangePatientPersonalDataImportService"),
    I_SCHR_4(4, "erpChangePatientPersonalDataHandlerService"),
    I_SCHR_5(5, "erpChangePatientPoliciesImportService"),
    I_SCHR_7(7, "lastAnthropometryImportService"),
    I_SCHR_8(8, "lastAnthropometryHandlerService"),
    I_SCHR_9(9, "patientConsentsImportService"),
    I_SCHR_11(11, "schoolAttachmentEventImportService")
    ;

    private final Integer plannerId;

    private final String plannerName;

    PlannersEnum(Integer plannerId, String plannerName) {
        this.plannerId = plannerId;
        this.plannerName = plannerName;
    }

    public Integer getPlannerId() {
        return plannerId;
    }

    public String getPlannerName() {
        return plannerName;
    }
}
