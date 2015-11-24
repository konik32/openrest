package pl.openrest.filters.query;

import java.io.Serializable;

import org.springframework.data.mapping.PersistentProperty;

import pl.openrest.predicate.parser.FilterPart;
import pl.openrest.predicate.parser.PredicateParts;

public interface PredicateContextBuilder {

    PredicateContextBuilder withFilterTree(FilterPart tree);

    PredicateContextBuilder withId(PersistentProperty<?> idProperty, Serializable id);

    PredicateContextBuilder withPredicateParts(PredicateParts predicateParts);

    PredicateContextBuilder withStaticFilters();

    PredicateContext<?> build();
}
