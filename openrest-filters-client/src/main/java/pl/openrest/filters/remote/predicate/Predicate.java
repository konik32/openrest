package pl.openrest.filters.remote.predicate;

import org.springframework.core.convert.ConversionService;

public interface Predicate {

    String toString(ConversionService conversionService);
}
