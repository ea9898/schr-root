package moscow.ptnl.domain.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.Serializable;

/**
 * @author mkomlev
 * Конвертер Integer в nullable флаг
 */
@Converter
public class BooleanIntegerConverter implements AttributeConverter<Boolean, Integer>, Serializable {
    
	private static final long serialVersionUID = 1241413503230982564L;

	@Override
    public Integer convertToDatabaseColumn(Boolean value) {
        if (value == null) {
            return null;
        }

        if (value) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public Boolean convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }
        return 1 == value.byteValue();
    }

}
