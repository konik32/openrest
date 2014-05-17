package pl.stalkon.data.boost.response.filter;

import java.util.HashMap;
import java.util.Map;

public class FiltersContext {
	private Map<String, Object> variables = new HashMap<String, Object>();

	public void add(String name, Object value, Class<?> clazz) {
		variables.put(getName(name, clazz), value);
	}

	public Object get(String name, Class<?> clazz) {
		return variables.get(getName(name, clazz));
	}
	

	public boolean contains(String name, Class<?> clazz) {
		return variables.containsKey(getName(name, clazz));
	}

	private String getName(String name, Class<?> clazz) {
		return clazz.getSimpleName() + "." + name;
	}


}
