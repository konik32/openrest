package pl.openrest.filters.remote.predicate;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class SortPredicate extends AbstractPredicate {

    public SortPredicate(String name, Object... parameters) {
        super(name, parameters);
    }

}
