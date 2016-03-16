package pl.openrest.filters.predicate;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.Predicate.PredicateType;
import pl.openrest.filters.query.JoinInformation;

@Getter
@ToString
@EqualsAndHashCode
public class PredicateInformation {

    protected final String name;
    protected final Method method;
    protected final PredicateType type;
    protected final boolean defaultedPageable;
    protected @Setter List<? extends JoinInformation> joins = Collections.emptyList();

    public PredicateInformation(Method method, Predicate predicateAnn) {
        this.name = predicateAnn.name().isEmpty() ? method.getName() : predicateAnn.name();
        this.method = method;
        this.type = predicateAnn.type();
        this.defaultedPageable = predicateAnn.defaultedPageable();
    }

    public PredicateInformation(Method method) {
        this.name = method.getName();
        this.method = method;
        this.type = PredicateType.STATIC_FILTER;
        this.defaultedPageable = true;
    }

}
