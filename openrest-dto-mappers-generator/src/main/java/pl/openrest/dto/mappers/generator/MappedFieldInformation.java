package pl.openrest.dto.mappers.generator;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.annotations.Nullable;

public class MappedFieldInformation {

	protected final PropertyDescriptor propertyDescriptor;
	protected final Field field;

	public MappedFieldInformation(Field field, boolean isEntity) {
		this.field = field;
		this.propertyDescriptor = BeanUtils.getPropertyDescriptor(
				getDeclaringClass(), getName());
		if (propertyDescriptor == null)
			throw new IllegalStateException(String.format(
					"Could not create PropertyDescriptor for field: %s.%s", field.getDeclaringClass(),
					field.getName()));
		if (propertyDescriptor.getReadMethod() == null)
			throw new IllegalStateException(String.format(
					"Field %s.%s does not have mandatory getter method",
					field.getDeclaringClass(), field.getName()));
		if (isEntity && propertyDescriptor.getWriteMethod() == null) {
			throw new IllegalStateException(String.format(
					"Field %s.%s does not have mandatory setter method",
					field.getDeclaringClass(), field.getName()));
		}
	}

	public String getName() {
		return field.getName();
	}

	public String getGetterName() {
		return propertyDescriptor.getReadMethod().getName();
	}

	public String getSetterName() {
		if (propertyDescriptor.getWriteMethod() != null)
			return propertyDescriptor.getWriteMethod().getName();
		return null;
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
		return AnnotationUtils.getAnnotation(getType(), Dto.class) != null;
	}

	public Class<?> getEntityType() {
		if (!isDto())
			throw new IllegalStateException(String.format(
					"%s field type is not dto", toString()));
		return AnnotationUtils.getAnnotation(getType(), Dto.class).entityType();
	}

	public String getNullableGetterName() {
		if (!isNullable())
			throw new IllegalStateException("Field is not nullable");
		Nullable nullable = field.getAnnotation(Nullable.class);
		Field nullableField = ReflectionUtils.findField(getDeclaringClass(),
				nullable.value());
		if (nullableField == null)
			throw new IllegalStateException(String.format(
					"%s doesn't have %s property", getDeclaringClass(),
					nullable.value()));
		try {
			PropertyDescriptor nullablePropertyDescriptor = new PropertyDescriptor(
					nullable.value(), getDeclaringClass());
			return nullablePropertyDescriptor.getReadMethod().getName();
		} catch (IntrospectionException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String toString() {
		return String.format("%s.%s", getDeclaringClass().getName(), getName());
	}

	public boolean isPrimitive() {
		return field.getType().isPrimitive();
	}

}
