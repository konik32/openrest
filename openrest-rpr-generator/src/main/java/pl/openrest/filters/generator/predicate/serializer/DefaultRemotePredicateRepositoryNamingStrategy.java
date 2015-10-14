package pl.openrest.filters.generator.predicate.serializer;

public class DefaultRemotePredicateRepositoryNamingStrategy implements RemotePredicateRepositoryNamingStrategy {

    private static final String CLASS_NAME_FORMAT = "R%s";

    @Override
    public String getPackageName(String predicateRepositoryPackageName) {
        return predicateRepositoryPackageName;
    }

    @Override
    public String getClassName(String predicateRepositoryClassName) {
        return String.format(CLASS_NAME_FORMAT, predicateRepositoryClassName);
    }

}
