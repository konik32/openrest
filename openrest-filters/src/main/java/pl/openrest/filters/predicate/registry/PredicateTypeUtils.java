package pl.openrest.filters.predicate.registry;

import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.NumberExpression;

import pl.openrest.filters.predicate.annotation.Predicate.PredicateType;

public final class PredicateTypeUtils {

    private PredicateTypeUtils() {
    }

    public static void verifyMethodReturnTypeMatchesPredicateType(Class<?> returnType, PredicateType type) {
        switch (type) {
        case SEARCH:
        case FILTER:
        case STATIC_FILTER:
            if (!BooleanExpression.class.isAssignableFrom(returnType))
                throw new IllegalArgumentException("Predicate of type SEARCH, FILTER or StaticFilters should return BooleanExpression");
            break;
        case SORT:
            if (!NumberExpression.class.isAssignableFrom(returnType))
                throw new IllegalArgumentException("Predicate of type SORT should return NumberExpression");
            break;
        }
    }
}
