package pl.openrest.filters.remote.predicate;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class FilterPredicate extends AbstractPredicate {

    public FilterPredicate(String name, Object... parameters) {
        super(name, parameters);
    }

}
