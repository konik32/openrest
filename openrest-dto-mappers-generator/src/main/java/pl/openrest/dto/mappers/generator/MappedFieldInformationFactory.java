package pl.openrest.dto.mappers.generator;

import java.lang.reflect.Field;
import java.util.Collection;

public class MappedFieldInformationFactory {

    public static MappedFieldInformation create(Field field) {
        if (Collection.class.isAssignableFrom(field.getType()))
            return new MappedCollectionFieldInformation(field);
        return new MappedFieldInformation(field);
    }

}
