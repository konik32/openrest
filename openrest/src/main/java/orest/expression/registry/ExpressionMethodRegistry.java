package orest.expression.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressionMethodRegistry {

	private Map<String, ExpressionMethodInformation> registry = new HashMap<String, ExpressionMethodInformation>();
	private List<ExpressionMethodInformation> staticFilters = new ArrayList<ExpressionMethodInformation>();
	
	public void add(ExpressionMethodInformation information) {
		registry.put(information.getName(), information);
	}
	
	public void addStaticFilter(ExpressionMethodInformation information) {
		staticFilters.add(information);
	}
	
	public ExpressionMethodInformation get(String name) {
		return registry.get(name);
	}

	public List<ExpressionMethodInformation> getStaticFilters() {
		return Collections.unmodifiableList(staticFilters);
	}

}
