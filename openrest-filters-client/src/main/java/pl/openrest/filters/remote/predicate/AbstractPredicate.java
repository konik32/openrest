package pl.openrest.filters.remote.predicate;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import org.springframework.core.convert.ConversionService;

@Getter
@EqualsAndHashCode
public abstract class AbstractPredicate implements Predicate {
    protected final String name;
    protected final Object parameters[];

    public AbstractPredicate(String name, Object... parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String toString(ConversionService conversionService) {
        return parameters.length > 0 ? String.format("%s(%s)", name, getSerializedParameters(conversionService)) : name;
    }

    @Override
    public String toString() {
        String parametersStr[] = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parametersStr[i] = parameters[i].toString();
        }
        return parameters.length > 0 ? String.format("%s(%s)", name, String.join(",", parametersStr)) : name;
    }

    private String getSerializedParameters(ConversionService conversionService) {
        String paramtersStr[] = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] != null) {
                paramtersStr[i] = conversionService.convert(parameters[i], String.class);
            } else {
                paramtersStr[i] = "";
            }
        }
        return String.join(";", paramtersStr);
    }

}
