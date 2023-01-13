package moscow.ptnl.app.domain.model.es;

import org.springframework.util.StringUtils;

import java.util.Objects;

public enum Gender {

    MALE("1"),
    FEMALE("2"),
    UNDEFINED("3");

    private String code;

    Gender(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Gender findByCode(String code) {
        code = StringUtils.trimWhitespace(code);

        for (Gender gender : Gender.values()) {
            if (Objects.equals(gender.code, code)) {
                return gender;
            }
        }
        return null;
    }
}
