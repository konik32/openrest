package pl.openrest.dto.mappers.generator;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.springframework.core.annotation.AnnotationUtils;

import pl.openrest.dto.annotations.Dto;

public class MappedCollectionFieldInformation extends MappedFieldInformation {

    public MappedCollectionFieldInformation(Field field) {
        super(field);
        if (!Collection.class.isAssignableFrom(field.getType()))
            throw new IllegalArgumentException(String.format("%s is not a Collection", toString()));
    }

    public Class<?> getRawType() {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    @Override
    public boolean isDto() {
        return AnnotationUtils.getAnnotation(getRawType(), Dto.class) != null;
    }

    @Override
    public Class<?> getEntityType() {
        if (!isDto())
            throw new IllegalStateException(String.format("%s field raw type is not dto", toString()));
        return AnnotationUtils.getAnnotation(getRawType(), Dto.class).entityType();
    }

}
