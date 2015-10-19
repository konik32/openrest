package pl.openrest.generator.commons.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.ConfigurationAware;
import pl.openrest.generator.commons.Initializable;

public class DefaultEntityInformationRegistry implements EntityInformationRegistry, ConfigurationAware, Initializable {

    private Map<Class<?>, EntityInformation> registry = new HashMap<>();
    private List<Class<? extends Annotation>> entityAnnotations;

    private Reflections reflections;

    @Override
    public void setConfiguration(Configuration configuration) {
        this.reflections = configuration.get("reflections");
        this.entityAnnotations = configuration.get("entityAnnotations");
    }

    @Override
    public void afterPropertiesSet() {
        if (entityAnnotations != null && !entityAnnotations.isEmpty())
            populateRegistry();
    }

    private void populateRegistry() {
        Map<Class<?>, Class<?>> entityRepoMap = getEntityRepositoryMap();
        Set<Class<?>> entitiesTypes = getEntitiesTypes();
        for (Class<?> entityType : entitiesTypes) {
            registry.put(entityType, new DefaultEntityInformation(entityType, entityRepoMap.get(entityType)));
        }
    }

    private Set<Class<?>> getEntitiesTypes() {
        Set<Class<?>> entitiesTypes = new HashSet<>();
        for (Class<? extends Annotation> ann : entityAnnotations) {
            entitiesTypes.addAll(reflections.getTypesAnnotatedWith(ann));
        }
        return entitiesTypes;
    }

    private Map<Class<?>, Class<?>> getEntityRepositoryMap() {
        Set<Class<?>> repositoriesTypes = new HashSet<>();
        repositoriesTypes.addAll(reflections.getTypesAnnotatedWith(RepositoryRestResource.class, true));
        repositoriesTypes.addAll(reflections.getTypesAnnotatedWith(RestResource.class, true));
        Map<Class<?>, Class<?>> entityRepoMap = new HashMap<>(repositoriesTypes.size());
        for (Class<?> type : repositoriesTypes) {
            for (Type i : type.getGenericInterfaces()) {
                if (i instanceof ParameterizedType && Repository.class.isAssignableFrom((Class<?>) ((ParameterizedType) i).getRawType())) {
                    ParameterizedType pType = (ParameterizedType) i;
                    entityRepoMap.put((Class<?>) pType.getActualTypeArguments()[0], type);
                }
            }
        }
        return entityRepoMap;
    }

    @Override
    public EntityInformation get(Class<?> entityType) {
        return registry.get(entityType);
    }

    @Override
    public boolean contains(Class<?> entityType) {
        return registry.containsKey(entityType);
    }

}
