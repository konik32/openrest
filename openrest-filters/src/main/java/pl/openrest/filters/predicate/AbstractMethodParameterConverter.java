package pl.openrest.filters.predicate;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.hateoas.core.MethodParameters;

public abstract class AbstractMethodParameterConverter implements MethodParameterConverter {

    private final static Object[] EMPTY_PARAMETERS = new Object[0];
    private final static String ILLEGAL_PARAMETERS_COUNT_MESSAGE = "RawParameters' count does not match method parameters' count";

    private Object[] doConvert(Method method, String[] rawParameters) {
        List<MethodParameter> parameters = new MethodParameters(method).getParameters();
        if (parameters.isEmpty()) {
            return EMPTY_PARAMETERS;
        }
        Object[] result = new Object[parameters.size()];

        for (int i = 0; i < rawParameters.length; i++) {
            MethodParameter param = parameters.get(i);
            result[i] = doConvert(param, rawParameters[i]);
        }

        return result;
    }

    protected abstract Object doConvert(MethodParameter parameter, String rawParameter);

    @Override
    public Object[] convert(Method method, String[] rawParameters) {
        if (rawParameters == null) {
            if (method.getParameterCount() > 0)
                throw new IllegalArgumentException(ILLEGAL_PARAMETERS_COUNT_MESSAGE);
            else
                return EMPTY_PARAMETERS;
        } else if (rawParameters.length != method.getParameterCount())
            throw new IllegalArgumentException(ILLEGAL_PARAMETERS_COUNT_MESSAGE);
        return doConvert(method, rawParameters);
    }

}
