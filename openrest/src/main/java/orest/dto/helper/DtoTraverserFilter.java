package orest.dto.helper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.core.annotation.AnnotationUtils;

import orest.dto.Dto;
import orest.util.traverser.TraverserFieldFilter;

public class DtoTraverserFilter implements TraverserFieldFilter {

	@Override
	public boolean matches(Field field, Object owner, String path) {
		if (Iterable.class.isAssignableFrom(field.getType())) {
			Type type = field.getGenericType();
			if (type instanceof ParameterizedType) {
				ParameterizedType pType = (ParameterizedType) type;
				Type[] arr = pType.getActualTypeArguments();
				return AnnotationUtils.isAnnotationDeclaredLocally(Dto.class, (Class<?>) arr[0]);
			} else {
				// Collections must be parameterized;
				return false;
			}
		} else {
			return AnnotationUtils.isAnnotationDeclaredLocally(Dto.class, field.getType());
		}
	}

}
