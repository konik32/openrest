package orest.expression.registry;

import java.util.List;

import lombok.Data;
import orest.expression.registry.ExpressionMethodInformation.Join;

@Data
public class ProjectionInfo {
	private final List<Join> expands;
	private final Class<?> projectionType;
}
