package pl.openrest.filters.querydsl.predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

import pl.openrest.filters.predicate.PredicateRepository;
import pl.openrest.filters.predicate.registry.PredicateInformation;
import pl.openrest.filters.query.registry.StaticFilterInformation;

public class QPredicateRepository implements PredicateRepository {

    private final Map<String, PredicateInformation> predicateInfoRegistry = new HashMap<String, PredicateInformation>();
    private final List<StaticFilterInformation> staticFilters = new ArrayList<StaticFilterInformation>();
    private final Object predicateRepository;
    private final boolean defaultedPageable;

    public QPredicateRepository(Object predicateRepository, boolean defaultedPageable) {
        this.predicateRepository = predicateRepository;
        this.defaultedPageable = defaultedPageable;
    }

    @Override
    public Object getPredicate(PredicateInformation predicateInfo, Object[] parameters) {
        return ReflectionUtils.invokeMethod(predicateInfo.getMethod(), predicateRepository, parameters);
    }

    @Override
    public Object getPredicate(String predicateName, Object[] parameters) {
        PredicateInformation predicateInfo = getPredicateInformation(predicateName);
        return getPredicate(predicateInfo, parameters);
    }

    @Override
    public List<StaticFilterInformation> getStaticFilters() {
        return Collections.unmodifiableList(staticFilters);
    }

    @Override
    public PredicateInformation getPredicateInformation(String predicateName) {
        PredicateInformation predicateInfo = predicateInfoRegistry.get(predicateName);
        if (predicateInfo == null)
            throw new IllegalArgumentException("No such predicate " + predicateName);
        return predicateInfo;
    }

    @Override
    public boolean isDefaultedPageable() {
        return defaultedPageable;
    }

}
