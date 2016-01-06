package pl.openrest.filters.query;

import lombok.NonNull;
import lombok.Setter;
import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.predicate.IdConverter;
import pl.openrest.filters.predicate.MethodParameterConverter;
import pl.openrest.filters.predicate.StaticFilterConditionEvaluator;

public abstract class AbstractPredicateContextBuilderFactory<T extends PredicateContextBuilder> implements
        PredicateContextBuilderFactory<T> {

    protected @Setter MethodParameterConverter predicateParameterConverter;
    protected @Setter MethodParameterConverter staticFiltersParameterConverter;
    protected @Setter IdConverter idConverter;
    protected @Setter StaticFilterConditionEvaluator staticFilterConditionEvaluator;

    public AbstractPredicateContextBuilderFactory(@NonNull MethodParameterConverter predicateParameterConverter,
            @NonNull MethodParameterConverter staticFiltersParameterConverter, @NonNull IdConverter idConverter) {
        this.predicateParameterConverter = predicateParameterConverter;
        this.staticFiltersParameterConverter = staticFiltersParameterConverter;
        this.idConverter = idConverter;
    }

    public abstract T create(FilterableEntityInformation entityInfo);
}
