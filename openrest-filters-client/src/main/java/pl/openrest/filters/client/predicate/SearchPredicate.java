package pl.openrest.filters.client.predicate;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Getter
public class SearchPredicate extends FilterPredicate {

    private final boolean defaultedPageable;

    public SearchPredicate(String name, boolean defaultedPageable, Object ... parameters) {
        super(name, parameters);
        this.defaultedPageable = defaultedPageable;
    }

}
