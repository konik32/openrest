package pl.openrest.filters.domain.registry;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pl.openrest.filters.predicate.registry.PredicateInformation;
import pl.openrest.filters.query.registry.StaticFilterInformation;
import pl.openrest.filters.repository.PredicateContextRepositoryInvoker;

@ToString
@EqualsAndHashCode
@Builder
public class FilterableEntityInformation {

    private final List<StaticFilterInformation> staticFilterRegistry;
    private final Map<String, PredicateInformation> predicateRegistry;
    private @Getter final Class<?> entityType;
    private @Getter final PredicateContextRepositoryInvoker repositoryInvoker;
    private @Getter final Object predicateRepository;
    private @Getter final boolean defaultedPageable;

    public List<StaticFilterInformation> getStaticFilters() {
        return Collections.unmodifiableList(staticFilterRegistry);
    }

    public PredicateInformation getPredicateInformation(String name) {
        return predicateRegistry.get(name);
    }
}
