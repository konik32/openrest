package pl.openrest.generator.commons.entity;

public interface EntityInformation {

    boolean isExported();

    String getPath();

    Class<?> getExcerptProjectionType();

    Class<?> getEntityType();

}
