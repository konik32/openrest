package pl.openrest.filters.client.predicate;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class FilterPredicate extends AbstractPredicate {

    public FilterPredicate(String name, Object... parameters) {
        super(name, parameters);
    }

}
