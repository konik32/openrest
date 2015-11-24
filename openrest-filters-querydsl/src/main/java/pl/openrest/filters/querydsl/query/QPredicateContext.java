package pl.openrest.filters.querydsl.query;

import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import pl.openrest.filters.query.PredicateContext;
import pl.openrest.filters.query.registry.JoinInformation;

import com.mysema.query.types.Predicate;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class QPredicateContext implements PredicateContext<Predicate> {

    private final List<JoinInformation> joins;
    private final @Getter Predicate predicate;

    public List<JoinInformation> getJoins() {
        return Collections.unmodifiableList(joins);
    }

}
