package pl.openrest.filters.domain.registry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.NonNull;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

import pl.openrest.filters.domain.registry.FilterableEntityInformation.FilterableEntityInformationBuilder;
import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.filters.predicate.registry.PredicateInformation;
import pl.openrest.filters.query.annotation.StaticFilter;
import pl.openrest.filters.query.registry.StaticFilterInformation;

public class FilterableEntityRegistry {

    private Map<Class<?>, FilterableEntityInformation> registry = new HashMap<>();

    private final PersistentEntities persistentEntities;

    public FilterableEntityRegistry(@NonNull PersistentEntities persistentEntities) {
        this.persistentEntities = persistentEntities;
    }

    public void register(Object predicateRepo) {
        PredicateRepository repoAnn = findAnnotation(predicateRepo);
        Class<?> entityType = repoAnn.value();

        FilterableEntityInformationBuilder builder = FilterableEntityInformation.builder();

        builder.defaultedPageable(repoAnn.defaultedPageable());
        builder.predicateRepository(predicateRepo);
        addPersistentEntity(builder, entityType);
        addSubRegisters(builder, entityType, predicateRepo);

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

    private void addPersistentEntity(FilterableEntityInformationBuilder builder, Class<?> entityType) {
        PersistentEntity<?, ?> persistentEntity = persistentEntities.getPersistentEntity(entityType);
        if (persistentEntity == null)
            throw new IllegalArgumentException("No such entity: " + entityType);
        builder.persistentEntity(persistentEntity);
    }

    private void addSubRegisters(FilterableEntityInformationBuilder builder, final Class<?> entityType, Object predicateRepo) {
        final Map<String, PredicateInformation> predicateRegistry = new HashMap<>();
        final List<StaticFilterInformation> staticFilterRegistry = new ArrayList<>();
        ReflectionUtils.doWithMethods(predicateRepo.getClass(), new MethodCallback() {

            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Predicate predicateAnn = method.getAnnotation(Predicate.class);
                PredicateInformation predicateInfo;
                if (predicateAnn != null)
                    predicateInfo = new PredicateInformation(method, predicateAnn, entityType);
                else
                    predicateInfo = new PredicateInformation(method);
                predicateRegistry.put(predicateInfo.getName(), predicateInfo);
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
}
