package pl.openrest.filters.predicate;

import lombok.NonNull;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

public class ConversionServiceBasedMethodParameterConverter extends AbstractMethodParameterConverter {

    private final ConversionService conversionService;

    public ConversionServiceBasedMethodParameterConverter(@NonNull ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    protected Object doConvert(MethodParameter parameter, String rawParameter) {
        return conversionService.convert(rawParameter, TypeDescriptor.forObject(rawParameter), new TypeDescriptor(parameter));
    }

}
