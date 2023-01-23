package moscow.ptnl.app.ecppis.model;

public class Value {
    private Long id;
    private String code;
    private String value;

    public Value() {
    }

    public Value(Long id, String code, String value) {
        this.id = id;
        this.code = code;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
