package pl.openrest.filters.query.registry;

import java.util.HashMap;
import java.util.Map;

public class StaticFilterRegistry {
    private Map<String, StaticFilterInformation> registry = new HashMap<>();

    public void add(String name, StaticFilterInformation information) {
        registry.put(name, information);
    }

    public StaticFilterInformation get(String name) {
        return registry.get(name);
    }
}
