package pl.openrest.filters.predicate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.Predicate.PredicateType;
import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.filters.query.JoinInformation;
import pl.openrest.filters.query.StaticFilterInformation;
import pl.openrest.filters.query.annotation.Join;
import pl.openrest.filters.query.annotation.StaticFilter;

public abstract class AbstractPredicateRepository implements pl.openrest.filters.predicate.PredicateRepository {

    protected final Map<String, PredicateInformation> predicateInfoRegistry = new HashMap<String, PredicateInformation>();
    protected final List<StaticFilterInformation> staticFilters = new ArrayList<StaticFilterInformation>();
    protected final Object rawPredicateRepository;
    protected final boolean defaultedPageable;

    public AbstractPredicateRepository(Object predicateRepository) {
        this.rawPredicateRepository = predicateRepository;
        PredicateRepository repoAnn = findAnnotation(predicateRepository);
        this.defaultedPageable = repoAnn.defaultedPageable();
        addSubRegisters(repoAnn.value(), predicateRepository);
    }

    @Override
    public Object getPredicate(PredicateInformation predicateInfo, Object[] parameters) {
        return ReflectionUtils.invokeMethod(predicateInfo.getMethod(), rawPredicateRepository, parameters);
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

    @Override
    public List<Method> getSearchPredicates() {
        List<Method> methods = new LinkedList<Method>();
        for(PredicateInformation info: predicateInfoRegistry.values()){
            if(info.getType().equals(PredicateType.SEARCH))
                methods.add(info.getMethod());
        }
        return methods;
    }

    public static PredicateRepository findAnnotation(Object predicateRepo) {
        PredicateRepository repoAnn = AnnotationUtils.findAnnotation(predicateRepo.getClass(), PredicateRepository.class);
        if (repoAnn == null)
            throw new IllegalArgumentException("Predicate repository should be annotated with @PredicateRepository");
        return repoAnn;
    }

    private void addSubRegisters(final Class<?> entityType, Object predicateRepo) {
        final Map<String, PredicateInformation> predicateRegistry = new HashMap<>();
        final List<StaticFilterInformation> staticFilterRegistry = new ArrayList<>();
        ReflectionUtils.doWithMethods(predicateRepo.getClass(), new MethodCallback() {

            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Predicate predicateAnn = AnnotationUtils.findAnnotation(method, Predicate.class);
                PredicateInformation predicateInfo;
                if (predicateAnn != null) {
                    predicateInfo = new PredicateInformation(method, predicateAnn);
                    predicateInfo.setJoins(getJoins(predicateAnn.joins(), entityType));
                    predicateRegistry.put(predicateInfo.getName(), predicateInfo);
                } else {
                    predicateInfo = new PredicateInformation(method);
                }
                StaticFilter staticFilterAnn = method.getAnnotation(StaticFilter.class);
                if (staticFilterAnn != null) {
                    staticFilterRegistry.add(new StaticFilterInformation(staticFilterAnn, predicateInfo));
                }
            }
        }, new MethodFilter() {
            @Override
            public boolean matches(Method method) {
                return method.getAnnotation(Predicate.class) != null || method.getAnnotation(StaticFilter.class) != null;
            }

        });
        predicateInfoRegistry.putAll(predicateRegistry);
        staticFilters.addAll(staticFilterRegistry);
    }

    protected abstract List<? extends JoinInformation> getJoins(Join[] join, Class<?> entityType);
}
