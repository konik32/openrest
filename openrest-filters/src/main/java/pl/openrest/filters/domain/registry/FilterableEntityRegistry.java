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

import pl.openrest.filters.domain.registry.FilterableEntityInformation.FilterableEntityInformationBuilder;
import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.filters.predicate.registry.PredicateInformation;
import pl.openrest.filters.query.annotation.StaticFilter;
import pl.openrest.filters.query.registry.StaticFilterInformation;
import pl.openrest.filters.repository.PredicateContextQueryDslRepository;
import pl.openrest.filters.repository.PredicateContextRepositoryInvoker;

public class FilterableEntityRegistry implements ApplicationContextAware {

    private Map<Class<?>, FilterableEntityInformation> registry = new HashMap<>();

    private final Repositories repositories;

    public FilterableEntityRegistry(@NonNull Repositories repositories) {
        this.repositories = repositories;
    }

    private void register(Object predicateRepo) {
        PredicateRepository repoAnn = findAnnotation(predicateRepo);
        Class<?> entityType = repoAnn.value();

        FilterableEntityInformationBuilder builder = FilterableEntityInformation.builder();

        builder.defaultedPageable(repoAnn.defaultedPageable());
        builder.predicateRepository(predicateRepo);
        builder.entityType(entityType);
        addSubRegisters(builder, entityType, predicateRepo);
        addPredicateContextRepositoryInvoker(entityType, builder);
        registry.put(entityType, builder.build());
    }

    public FilterableEntityInformation get(Class<?> entityType) {
        return registry.get(entityType);
    }

    private PredicateRepository findAnnotation(Object predicateRepo) {
        PredicateRepository repoAnn = AnnotationUtils.findAnnotation(predicateRepo.getClass(), PredicateRepository.class);
        if (repoAnn == null)
            throw new IllegalArgumentException("Predicate repository should be annotated with @PredicateRepository");
        return repoAnn;
    }

    private void addSubRegisters(FilterableEntityInformationBuilder builder, final Class<?> entityType, Object predicateRepo) {
        final Map<String, PredicateInformation> predicateRegistry = new HashMap<>();
        final List<StaticFilterInformation> staticFilterRegistry = new ArrayList<>();
        ReflectionUtils.doWithMethods(predicateRepo.getClass(), new MethodCallback() {

            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Predicate predicateAnn = method.getAnnotation(Predicate.class);
                PredicateInformation predicateInfo;
                if (predicateAnn != null) {
                    predicateInfo = new PredicateInformation(method, predicateAnn, entityType);
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
        builder.predicateRegistry(predicateRegistry);
        builder.staticFilterRegistry(staticFilterRegistry);
    }

    private void addPredicateContextRepositoryInvoker(Class<?> entityType, FilterableEntityInformationBuilder builder) {
        Object repository = repositories.getRepositoryFor(entityType);
        if (repository instanceof PredicateContextQueryDslRepository)
            builder.repositoryInvoker(new PredicateContextRepositoryInvoker((PredicateContextQueryDslRepository) repository));
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
