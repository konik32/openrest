package orest.repository;

import java.util.ArrayList;
import java.util.List;

import orest.expression.registry.ExpressionMethodInformation;
import orest.expression.registry.ExpressionMethodInformation.Join;

import org.springframework.data.mapping.PropertyPath;

import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.PathBuilderFactory;

public class ExpressionUtils {
	
	private ExpressionUtils(){
		
	}

	private static PathBuilderFactory pathBuilderFactory = new PathBuilderFactory();
	
	public static List<Join> getJoins(String joinPath, PathBuilder<?> builder,
			Class<?> entityType, Boolean fetch) {
		PropertyPath propertyPath = PropertyPath.from(joinPath, entityType);
		List<Join> joins = new ArrayList<Join>();
		String dotPath = "";
		while (propertyPath != null) {
			Path<?> path = null;
			boolean collection = false;
			dotPath += dotPath.isEmpty()? propertyPath.getSegment(): "." + propertyPath.getSegment();
			if (propertyPath.isCollection()) {
				path = builder.getCollection(dotPath,
						propertyPath.getType());
				collection = true;
			} else {
				path = builder.get(dotPath,
						propertyPath.getType());
			}
			joins.add(new ExpressionMethodInformation.Join(path, collection,
					fetch, propertyPath.getType()));
			propertyPath = propertyPath.next();
		}
		return joins;
	}
	
	public static PathBuilder<?> getPathBuilder(Class<?> entityType){
		return pathBuilderFactory.create(entityType);
	}
}
