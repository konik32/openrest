package orest.expression.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orest.expression.registry.ExpressionMethodInformation.Join;
import orest.repository.ExpressionUtils;

import com.mysema.query.types.path.PathBuilder;

public class ProjectionExpandsRegistry {

	private Map<String, List<Join>> expands = new HashMap<String, List<Join>>();

	public void addExpand(String projectionName, String expandString, Class<?> entityType, PathBuilder builder) {
		String expands[] = expandString.split(",");
		List<Join> joins = new ArrayList<Join>();
		for (String expand : expands) {
			joins.addAll(ExpressionUtils.getJoins(expand, builder, entityType, true));
		}
		this.expands.put(projectionName, joins);
	}

	public List<Join> getExpands(String projectionName) {
		return expands.get(projectionName);
	}
}
