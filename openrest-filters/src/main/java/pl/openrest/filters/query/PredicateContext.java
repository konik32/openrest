package pl.openrest.filters.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.openrest.filters.query.registry.JoinInformation;

import com.mysema.query.types.Predicate;

@ToString
@EqualsAndHashCode
public class PredicateContext {

    private List<JoinInformation> joins = new ArrayList<JoinInformation>();
    private @Getter @Setter Predicate predicate;

    public void addJoin(JoinInformation join) {
        if (join != null)
            joins.add(join);
    }

    public void addJoins(List<JoinInformation> joins) {
        if (joins != null)
            this.joins.addAll(joins);
    }

    public List<JoinInformation> getJoins() {
        return Collections.unmodifiableList(joins);
    }

}
