package moscow.ptnl.app.domain.model.es;

import org.springframework.util.StringUtils;

import java.util.Objects;

public enum Gender {

    MALE(1L),
    FEMALE(2L),
    UNDEFINED(3L);

    private final Long code;

    Gender(Long code) {
        this.code = code;
    }

    public Long getCode() {
        return code;
    }

    public static Gender findByCode(String code) {
        code = StringUtils.trimWhitespace(code);

        for (Gender gender : Gender.values()) {
            if (Objects.equals(gender.code.toString(), code)) {
                return gender;
            }
        }
        return null;
    }
}
