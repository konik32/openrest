package pl.openrest.filters.predicate.registry;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.Predicate.PredicateType;
import pl.openrest.filters.query.registry.JoinInformation;

@Getter
@ToString
@EqualsAndHashCode
public class PredicateInformation {

    private final String name;
    private final Method method;
    private final PredicateType type;
    private final boolean defaultedPageable;
    private final List<JoinInformation> joins;

    public PredicateInformation(Method method, Predicate predicateAnn, List<JoinInformation> joins) {
        this.name = predicateAnn.name();
        this.method = method;
        this.type = predicateAnn.type();
        this.defaultedPageable = predicateAnn.defaultedPageable();
        this.joins = joins;
    }

    public PredicateInformation(Method method, Predicate predicateAnn) {
        this(method, predicateAnn, Collections.EMPTY_LIST);
    }

}
