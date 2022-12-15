package moscow.ptnl.app.error;

public class ValidationParameter {
    
    private String code;
    private String value;

    public ValidationParameter() {
    }

    public ValidationParameter(String code, Object value) {
        this.code = code;
        this.value = value != null ? value.toString() : null;
    }

    public ValidationParameter(String code, String value) {
        this.code = code;
        this.value = value;
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

    public boolean match(String pattern) {
        if (value == null) {
            return false;
        }
        return value.matches(pattern);
    }

    public boolean notMatch(String pattern) {
        return !match(pattern);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidationParameter that = (ValidationParameter) o;

        return !(code != null ? !code.equals(that.code) : that.code != null) && !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}