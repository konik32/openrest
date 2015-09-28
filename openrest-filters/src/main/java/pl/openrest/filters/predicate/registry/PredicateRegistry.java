package pl.openrest.filters.predicate.registry;

import java.util.HashMap;
import java.util.Map;

public class PredicateRegistry {
    private Map<String, PredicateInformation> registry = new HashMap<String, PredicateInformation>();

    public void add(PredicateInformation information) {
        registry.put(information.getName(), information);
    }

    public PredicateInformation get(String name) {
        return registry.get(name);
    }

}
