package pl.openrest.filters.generator.predicate.serializer;

import java.lang.String;
import javax.annotation.Generated;
import pl.openrest.filters.remote.predicate.FilterPredicate;
import pl.openrest.filters.remote.predicate.SearchPredicate;

@Generated("pl.openrest.rpr.generator")
public final class RTestPredicateRepository {
    private static final String NAME_EQ = "nameEq";

    private static final String ACTIVE = "active";

    public static final boolean DEFAULTED_PAGEABLE = false;

    private RTestPredicateRepository() {
    }

    public static SearchPredicate nameEq(String name) {
        return new SearchPredicate(NAME_EQ, true, name);
    }

    public static FilterPredicate active() {
        return new FilterPredicate(ACTIVE);
    }
}