package pl.openrest.filters.predicate;

import java.lang.reflect.Method;
import java.util.List;

import pl.openrest.filters.query.StaticFilterInformation;

public interface PredicateRepository {

    Object getPredicate(PredicateInformation predicateInfo, Object[] parameters);

    Object getPredicate(String predicateName, Object[] parameters);

    List<StaticFilterInformation> getStaticFilters();

    PredicateInformation getPredicateInformation(String predicateName);
    
    List<Method> getSearchPredicates();

    boolean isDefaultedPageable();
}
