package pl.openrest.filters.querydsl.predicate;

import pl.openrest.filters.predicate.PredicateRepository;
import pl.openrest.filters.predicate.PredicateRepositoryFactory;

public class QPredicateRepositoryFactory implements PredicateRepositoryFactory {

    @Override
    public PredicateRepository create(Object rawPredicateRepository) {
        return new QPredicateRepository(rawPredicateRepository);
    }

}
