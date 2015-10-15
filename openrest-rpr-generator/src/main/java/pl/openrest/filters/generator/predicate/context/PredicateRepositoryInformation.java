package pl.openrest.filters.generator.predicate.context;

import java.util.LinkedList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pl.openrest.filters.remote.predicate.AbstractPredicate;

@Getter
@EqualsAndHashCode
@ToString
public class PredicateRepositoryInformation {

    private final Class<?> repositoryType;
    private final Class<?> entityType;
    private final boolean defaultedPageable;
    private List<AbstractPredicate> predicates;

    public PredicateRepositoryInformation(Class<?> repositoryType, Class<?> entityType, String packageName, boolean defaultedPageable) {
        this.repositoryType = repositoryType;
        this.entityType = entityType;
        this.defaultedPageable = defaultedPageable;
        predicates = new LinkedList<>();
    }

    public void addPredicate(AbstractPredicate predicate) {
        predicates.add(predicate);
    }

}
