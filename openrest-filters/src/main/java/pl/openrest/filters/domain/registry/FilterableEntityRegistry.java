package pl.openrest.filters.domain.registry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.NonNull;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

import pl.openrest.filters.predicate.AbstractPredicateRepository;
import pl.openrest.filters.predicate.PredicateRepositoryFactory;
import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.filters.predicate.registry.PredicateInformation;
import pl.openrest.filters.query.annotation.StaticFilter;
import pl.openrest.filters.query.registry.StaticFilterInformation;
import pl.openrest.filters.repository.PredicateContextRepository;
import pl.openrest.filters.repository.PredicateContextRepositoryInvoker;

public class FilterableEntityRegistry implements ApplicationContextAware {

    private Map<Class<?>, FilterableEntityInformation> registry = new HashMap<>();

    private final Repositories repositories;
    private final PredicateRepositoryFactory predicateRepositoryFactory;

    public FilterableEntityRegistry(@NonNull Repositories repositories, PredicateRepositoryFactory predicateRepositoryFactory) {
        this.repositories = repositories;
        this.predicateRepositoryFactory = predicateRepositoryFactory;
    }

    private void register(Object predicateRepo) {
        PredicateRepository repoAnn = AbstractPredicateRepository.findAnnotation(predicateRepo);
        Class<?> entityType = repoAnn.value();

        FilterableEntityInformation entityInfo = new FilterableEntityInformation(entityType,
                getPredicateContextRepositoryInvoker(entityType), predicateRepositoryFactory.create(predicateRepo));
        registry.put(entityType, entityInfo);
    }

    public FilterableEntityInformation get(Class<?> entityType) {
        return registry.get(entityType);
    }

    private PredicateContextRepositoryInvoker getPredicateContextRepositoryInvoker(Class<?> entityType) {
        Object repository = repositories.getRepositoryFor(entityType);
        if (repository instanceof PredicateContextRepository)
            return new PredicateContextRepositoryInvoker((PredicateContextRepository) repository);
        else
            throw new IllegalStateException("You must specify PredicateContextQueryDslRepository for " + entityType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        for (Object predicateRepository : applicationContext.getBeansWithAnnotation(PredicateRepository.class).values()) {
            register(predicateRepository);
        }
    }
}
