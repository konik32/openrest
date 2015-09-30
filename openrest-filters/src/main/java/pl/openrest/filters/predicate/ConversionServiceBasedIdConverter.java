package pl.openrest.filters.predicate;

import java.io.Serializable;

import lombok.NonNull;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.PersistentProperty;

public class ConversionServiceBasedIdConverter implements IdConverter {

    private final ConversionService conversionService;

    public ConversionServiceBasedIdConverter(@NonNull ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object convert(PersistentProperty idProperty, Serializable idValue) {
        return conversionService.convert(idValue, idProperty.getActualType());
    }

}
