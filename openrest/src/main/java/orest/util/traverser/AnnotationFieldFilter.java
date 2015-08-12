package orest.util.traverser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import lombok.NonNull;

public class AnnotationFieldFilter implements TraverserFieldFilter {

	private final Class<? extends Annotation> ann;

	public AnnotationFieldFilter(@NonNull Class<? extends Annotation> ann) {
		this.ann = ann;
	}

	@Override
	public boolean matches(Field field, Object owner, String path) {
		return field.isAnnotationPresent(ann);
	}
}
