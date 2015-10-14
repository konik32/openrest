package pl.openrest.filters.generator.predicate.serializer;

public interface RemotePredicateRepositoryNamingStrategy {

    String getPackageName(String predicateRepositoryPackageName);
    String getClassName(String predicateRepositoryClassName);
}
