package pl.openrest.generator.commons.entity;

import org.atteo.evo.inflector.English;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.util.StringUtils;

public class DefaultEntityInformation implements EntityInformation {

    private final Class<?> entityType;
    private final RepositoryRestResource repositoryAnnotation;
    private final RestResource annotation;

    public DefaultEntityInformation(Class<?> entityType, Class<?> repoType) {
        this.entityType = entityType;
        if (repoType != null) {
            this.repositoryAnnotation = repoType.getAnnotation(RepositoryRestResource.class);
            this.annotation = repoType.getAnnotation(RestResource.class);
        } else {
            this.repositoryAnnotation = null;
            this.annotation = null;
        }
    }

    @Override
    public boolean isExported() {

        if (repositoryAnnotation != null) {
            return repositoryAnnotation.exported();
        }

        if (annotation != null) {
            return annotation.exported();
        }

        return false;
    }

    @Override
    public String getPath() {

        String fallback = English.plural(StringUtils.uncapitalize(entityType.getSimpleName()));

        if (repositoryAnnotation != null) {
            String path = repositoryAnnotation.path();
            return StringUtils.hasText(path) ? path : fallback;
        }

        if (annotation != null) {
            String path = annotation.path();
            return StringUtils.hasText(path) ? path : fallback;
        }

        return fallback;
    }

    @Override
    public Class<?> getExcerptProjectionType() {
        if (repositoryAnnotation == null) {
            return null;
        }
        Class<?> excerptProjection = repositoryAnnotation.excerptProjection();
        return excerptProjection.equals(RepositoryRestResource.None.class) ? null : excerptProjection;
    }

    @Override
    public Class<?> getEntityType() {
        return entityType;
    }

}
