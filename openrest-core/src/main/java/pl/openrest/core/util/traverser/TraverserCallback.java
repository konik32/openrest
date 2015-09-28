package pl.openrest.core.util.traverser;

import java.lang.reflect.Field;

public interface TraverserCallback {

	void doWith(Field field, Object owner, String path) throws IllegalArgumentException, IllegalAccessException;
}
