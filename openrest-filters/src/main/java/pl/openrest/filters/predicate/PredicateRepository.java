package pl.openrest.filters.predicate;

import java.util.List;

import pl.openrest.filters.predicate.registry.PredicateInformation;
import pl.openrest.filters.query.registry.StaticFilterInformation;

public interface PredicateRepository {

    Object getPredicate(PredicateInformation predicateInfo, Object[] parameters);

    Object getPredicate(String predicateName, Object[] parameters);

    List<StaticFilterInformation> getStaticFilters();

    PredicateInformation getPredicateInformation(String predicateName);

    boolean isDefaultedPageable();
}
