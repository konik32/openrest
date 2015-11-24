package pl.openrest.filters.query;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;

public interface PredicateContextBuilderFactory<T extends PredicateContextBuilder> {

    T create(FilterableEntityInformation entityInfo);

}
