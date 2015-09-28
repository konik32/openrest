package pl.openrest.filters.predicate;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.Predicate.PredicateType;
import pl.openrest.filters.query.annotation.Join;
import pl.openrest.filters.query.annotation.StaticFilter;

@Getter
@ToString
@EqualsAndHashCode
public class PredicateInformation {

    private final String name;
    private final Method method;
    private final PredicateType type;
    private final boolean defaultedPageable;
    private final StaticFilter staticFilter;
    private final List<Join> joins;

    private PredicateInformation(Method method, Predicate predicateAnn, StaticFilter staticFilter) {
        this.name = predicateAnn.name();
        this.method = method;
        this.type = predicateAnn.type();
        this.defaultedPageable = predicateAnn.defaultedPageable();
        this.joins = Arrays.asList(predicateAnn.joins());
        this.staticFilter = staticFilter;
    }

    private PredicateInformation(Method method, Predicate predicateAnn) {
        this(method, predicateAnn, null);
    }
}
