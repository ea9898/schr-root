package moscow.ptnl.app.model;

public enum PlannersEnum {

    I_SCHR_1(1, "attachmentEventImportService"),
    I_SCHR_2(2, "attachmentEventHandlerService"),
    I_SCHR_3(3, "erpChangePatientPersonalDataImportService"),
    I_SCHR_5(3, "erpChangePatientPoliciesImportService"),
    I_SCHR_7(4, "lastAnthropometryImportService")
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
