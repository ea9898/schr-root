package moscow.ptnl.domain.entity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "CONFIG")
@Cacheable
public class Setting implements Serializable {

    private static final long serialVersionUID = -7504397229834601830L;

    @Id
    @Column(name = "CODE", unique = true, nullable = false)
    private String code;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "VAL", nullable = false)
    private String value;

    @Column(name = "DATE_UPDATED", nullable = false)
    private LocalDateTime updatedDate;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        moscow.ptnl.domain.entity.Setting setting = (moscow.ptnl.domain.entity.Setting) o;
        return Objects.equals(code, setting.code) &&
                Objects.equals(description, setting.description) &&
                Objects.equals(value, setting.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, description, value);
    }
}

