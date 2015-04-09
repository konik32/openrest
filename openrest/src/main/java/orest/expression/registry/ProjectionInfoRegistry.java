package orest.expression.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orest.expression.registry.ExpressionMethodInformation.Join;
import orest.repository.ExpressionUtils;

import com.mysema.query.types.path.PathBuilder;

public class ProjectionInfoRegistry {

	private Map<String, Map<Class<?>, ProjectionInfo>> projectionsInfo = new HashMap<String, Map<Class<?>, ProjectionInfo>>();

	public static List<Join> getExpands(String expandString, Class<?> entityType, PathBuilder builder) {
		String expands[] = expandString.split(",");
		List<Join> joins = new ArrayList<Join>();
		for (String expand : expands) {
			joins.addAll(ExpressionUtils.getJoins(expand, builder, entityType, true));
		}
		return joins;
	}

	public ProjectionInfo get(String projectionName, Class<?> entityType) {
		Map<Class<?>, ProjectionInfo> entitiesProjectionInfo = projectionsInfo.get(projectionName);
		if (entitiesProjectionInfo == null)
			return null;
		return entitiesProjectionInfo.get(entityType);
	}

	public void put(String projectionName, Class<?> entityType, ProjectionInfo projectionInfo) {
		Map<Class<?>, ProjectionInfo> entitiesProjectionInfo = this.projectionsInfo.get(projectionName);
		if (entitiesProjectionInfo == null) {
			entitiesProjectionInfo = new HashMap<Class<?>, ProjectionInfo>();
			this.projectionsInfo.put(projectionName, entitiesProjectionInfo);
		}
		entitiesProjectionInfo.put(entityType, projectionInfo);
	}
}
