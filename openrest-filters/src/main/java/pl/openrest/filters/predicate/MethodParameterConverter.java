package pl.openrest.filters.predicate;

import java.lang.reflect.Method;

public interface MethodParameterConverter {

    Object[] convert(Method method, String[] rawParameters);
}
