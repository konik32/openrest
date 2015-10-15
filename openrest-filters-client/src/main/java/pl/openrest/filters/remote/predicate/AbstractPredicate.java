package pl.openrest.filters.remote.predicate;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public abstract class AbstractPredicate {
    protected final String name;
    protected final Object parameters[];

    public AbstractPredicate(String name, Object... parameters) {
        this.name = name;
        this.parameters = parameters;
    }
}
