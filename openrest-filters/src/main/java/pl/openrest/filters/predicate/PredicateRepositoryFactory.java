package pl.openrest.filters.predicate;

public interface PredicateRepositoryFactory {

    PredicateRepository create(Object rawPredicateRepository);
}
