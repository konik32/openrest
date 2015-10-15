package pl.openrest.filters.remote.predicate;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class FilterPredicate extends AbstractPredicate {

    public FilterPredicate(String name, Object... parameters) {
        super(name, parameters);
    }

}
