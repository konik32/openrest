package pl.openrest.filters.predicate.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredicateRegistry {
    private Map<String, PredicateInformation> registry = new HashMap<String, PredicateInformation>();
    private List<PredicateInformation> staticFilters = new ArrayList<PredicateInformation>();

    public void add(PredicateInformation information) {
        registry.put(information.getName(), information);
    }

    public void addStaticFilter(PredicateInformation information) {
        staticFilters.add(information);
    }

    public PredicateInformation get(String name) {
        return registry.get(name);
    }

    public List<PredicateInformation> getStaticFilters() {
        return Collections.unmodifiableList(staticFilters);
    }
}
