package pl.openrest.dto.mappers.generator;

import java.lang.reflect.Field;
import java.util.Collection;

public class MappedFieldPairFactory {

    public static MappedFieldPair create(Field dtoField, Field entityField) {
        if (Collection.class.isAssignableFrom(dtoField.getType()))
            return new MappedCollectionFieldPair(new MappedCollectionFieldInformation(dtoField, false), new MappedCollectionFieldInformation(
                    entityField,false));
        return new MappedFieldPair(new MappedFieldInformation(dtoField,false), new MappedFieldInformation(entityField,false));
    }

}
