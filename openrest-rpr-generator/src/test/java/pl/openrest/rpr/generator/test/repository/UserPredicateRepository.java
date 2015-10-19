package pl.openrest.rpr.generator.test.repository;

import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.filters.predicate.annotation.Predicate.PredicateType;

@PredicateRepository(defaultedPageable = false, value = Object.class)
public class UserPredicateRepository {

    @Predicate(type = PredicateType.SEARCH)
    public void nameEq(String name) {
    }

    @Predicate(type = PredicateType.FILTER)
    public void active() {
    }

    @Predicate(type = PredicateType.SORT)
    public void sort(String address) {
    }
}
