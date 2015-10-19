package pl.openrest.generator.commons.entity;

public interface EntityInformationRegistry {

    EntityInformation get(Class<?> entityType);

    boolean contains(Class<?> entityType);
}
