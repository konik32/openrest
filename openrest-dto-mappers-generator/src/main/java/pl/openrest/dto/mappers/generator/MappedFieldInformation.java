package pl.openrest.dto.mappers.generator;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.annotations.Nullable;

public class MappedFieldInformation {

    protected final PropertyDescriptor propertyDescriptor;
    protected final Field field;

    public MappedFieldInformation(Field field) {
        try {
            this.field = field;
            this.propertyDescriptor = new PropertyDescriptor(field.getName(), getDeclaringClass());
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getName() {
        return field.getName();
    }

    public String getGetterName() {
        return propertyDescriptor.getReadMethod().getName();
    }

    public String getSetterName() {
        return propertyDescriptor.getReadMethod().getName();
    }

    public boolean isNullable() {
        return field.getAnnotation(Nullable.class) != null;
    }

    public Class<?> getType() {
        return field.getType();
    }

    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    public boolean isDto() {
        return field.getAnnotation(Dto.class) != null;
    }

    public Class<?> getEntityType() {
        if (!isDto())
            throw new IllegalStateException(String.format("%s field type is not dto", toString()));
        return field.getAnnotation(Dto.class).entityType();
    }

    public String getNullableGetterName() {
        if (!isNullable())
            throw new IllegalStateException("Field is not nullable");
        Nullable nullable = field.getAnnotation(Nullable.class);
        Field nullableField = ReflectionUtils.findField(getDeclaringClass(), nullable.value());
        if (nullableField == null)
            throw new IllegalStateException(String.format("%s doesn't have %s property", getDeclaringClass(), nullableField));
        try {
            PropertyDescriptor nullablePropertyDescriptor = new PropertyDescriptor(nullable.value(), getDeclaringClass());
            return nullablePropertyDescriptor.getReadMethod().getName();
        } catch (IntrospectionException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s.%s", getDeclaringClass().getName(), getName());
    }

}
