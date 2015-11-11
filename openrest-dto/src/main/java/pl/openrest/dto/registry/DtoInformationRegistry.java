package pl.openrest.dto.registry;

import java.util.HashMap;
import java.util.Map;

public class DtoInformationRegistry {

    private Map<Class<?>, DtoInformation> mapping = new HashMap<Class<?>, DtoInformation>();
    private Map<String, DtoInformation> namesMapping = new HashMap<String, DtoInformation>();

    public DtoInformation get(Class<?> dtoType) {
        return mapping.get(dtoType);
    }

    public DtoInformation get(String name) {
        return namesMapping.get(name);
    }

    public boolean contains(Class<?> dtoType) {
        return mapping.containsKey(dtoType);
    }

    public boolean contains(String name) {
        return namesMapping.containsKey(name);
    }

    public void put(String name, DtoInformation dtoInfo) {
        namesMapping.put(name, dtoInfo);
    }

    public void put(Class<?> clazz, DtoInformation dtoInfo) {
        mapping.put(clazz, dtoInfo);
    }

}
