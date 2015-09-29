package pl.openrest.filters.query;

import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import pl.openrest.filters.query.registry.JoinInformation;

import com.mysema.query.types.Predicate;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class PredicateContext {

    private final List<JoinInformation> joins;
    private final @Getter Predicate predicate;

    public List<JoinInformation> getJoins() {
        return Collections.unmodifiableList(joins);
    }

}
