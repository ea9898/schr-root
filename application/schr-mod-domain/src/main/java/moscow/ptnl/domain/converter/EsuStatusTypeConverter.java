package moscow.ptnl.domain.converter;

import moscow.ptnl.domain.entity.esu.EsuStatusType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * @author sorlov
 */
@Converter
public class EsuStatusTypeConverter implements AttributeConverter<EsuStatusType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(EsuStatusType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.ordinal();
    }

    @Override
    public EsuStatusType convertToEntityAttribute(Integer dbData) {
        if (dbData == null || EsuStatusType.values().length <= dbData) {
            return null;
        }
        return EsuStatusType.values()[dbData];
    }
}
