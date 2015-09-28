package pl.openrest.filters.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.openrest.filters.query.registry.JoinInformation;

public class PredicateContext {

    private List<JoinInformation> joins = new ArrayList<JoinInformation>();

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
