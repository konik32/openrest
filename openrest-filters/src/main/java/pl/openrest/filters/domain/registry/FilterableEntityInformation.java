package pl.openrest.filters.domain.registry;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import pl.openrest.filters.predicate.PredicateRepository;
import pl.openrest.filters.repository.PredicateContextRepositoryInvoker;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class FilterableEntityInformation {

    private @Getter final Class<?> entityType;
    private @Getter final PredicateContextRepositoryInvoker repositoryInvoker;
    private @Getter final PredicateRepository predicateRepository;

}
