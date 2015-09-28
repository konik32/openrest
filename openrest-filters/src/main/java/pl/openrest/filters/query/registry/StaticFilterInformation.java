package pl.openrest.filters.query.registry;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pl.openrest.filters.predicate.registry.PredicateInformation;
import pl.openrest.filters.query.annotation.StaticFilter;

@Getter
@ToString
@EqualsAndHashCode
public class StaticFilterInformation {
    private final String condition;
    private final String[] parameters;
    private final PredicateInformation predicateInformation;

    public StaticFilterInformation(StaticFilter staticFilterAnn, PredicateInformation predicateInformation) {
        this.condition = staticFilterAnn.offOnCondition();
        this.parameters = staticFilterAnn.parameters();
        this.predicateInformation = predicateInformation;
    }
}
