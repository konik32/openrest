package pl.openrest.filters.predicate.registry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.Predicate.PredicateType;
import pl.openrest.filters.query.annotation.Join;
import pl.openrest.filters.query.registry.JoinInformation;
import pl.openrest.filters.query.registry.JoinInformationBuilder;

@Getter
@ToString
@EqualsAndHashCode
public class PredicateInformation {

    private final String name;
    private final Method method;
    private final PredicateType type;
    private final boolean defaultedPageable;
    private final List<JoinInformation> joins;

    public PredicateInformation(Method method, Predicate predicateAnn, Class<?> entityType) {
        this.name = predicateAnn.name().isEmpty() ? method.getName() : predicateAnn.name();
        this.method = method;
        this.type = predicateAnn.type();
        PredicateTypeUtils.verifyMethodReturnTypeMatchesPredicateType(method.getReturnType(), type);
        this.defaultedPageable = predicateAnn.defaultedPageable();
        List<JoinInformation> joins = new ArrayList<>();
        for (Join join : predicateAnn.joins()) {
            joins.addAll(JoinInformationBuilder.getJoinsInformation(join.value(), entityType, join.fetch()));
        }
        this.joins = joins;
    }

    public PredicateInformation(Method method) {
        this.name = method.getName();
        this.method = method;
        this.type = PredicateType.STATIC_FILTER;
        PredicateTypeUtils.verifyMethodReturnTypeMatchesPredicateType(method.getReturnType(), type);
        this.defaultedPageable = true;
        this.joins = Collections.<JoinInformation> emptyList();
    }

}
