package orest.expression.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orest.expression.registry.ExpressionMethodInformation.Join;
import orest.repository.ExpressionUtils;

import com.mysema.query.types.path.PathBuilder;

public class ProjectionExpandsRegistry {

	private Map<String, Map<Class<?>, List<Join>>> expands = new HashMap<String, Map<Class<?>, List<Join>>>();

	public void addExpand(String projectionName, String expandString, Class<?> entityType, PathBuilder builder) {
		String expands[] = expandString.split(",");
		List<Join> joins = new ArrayList<Join>();
		for (String expand : expands) {
			joins.addAll(ExpressionUtils.getJoins(expand, builder, entityType, true));
		}
		Map<Class<?>, List<Join>> entityProjectionExpands = this.expands.get(projectionName);
		if (entityProjectionExpands == null)
			entityProjectionExpands = new HashMap<Class<?>, List<Join>>();
		entityProjectionExpands.put(entityType, joins);
		this.expands.put(projectionName, entityProjectionExpands);
	}

	public List<Join> getExpands(String projectionName, Class<?> entityType) {
		Map<Class<?>, List<Join>> entityProjectionExpands = expands.get(projectionName);
		if (entityProjectionExpands == null)
			return null;
		return entityProjectionExpands.get(entityType);
	}
}
