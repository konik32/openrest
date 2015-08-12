package orest.util.traverser;

import java.lang.reflect.Field;

import org.springframework.data.mapping.PropertyPath;

public interface TraverserFieldFilter {

	boolean matches(Field field, Object owner, String path);
}
