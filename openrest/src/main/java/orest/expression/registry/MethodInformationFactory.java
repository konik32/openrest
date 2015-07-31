package orest.expression.registry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import orest.expression.registry.ExpressionMethodInformation.MethodType;
import orest.repository.ExpressionUtils;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.core.AnnotationAttribute;
import org.springframework.hateoas.core.MethodParameters;
import org.springframework.util.Assert;

import com.mysema.query.types.path.PathBuilder;

public class MethodInformationFactory {
	
	private MethodInformationFactory() {
	}

	private static final AnnotationAttribute PARAM_VALUE = new AnnotationAttribute(
			Param.class);

	public static ExpressionMethodInformation create(Class<?> entityType,
			Method method, PathBuilder<?> pathBuilder) {
		Assert.notNull(entityType);
		Assert.notNull(method);
		Assert.notNull(pathBuilder);
		ExpressionMethod annotation = getAnnotation(method);
		ExpressionMethodInformation methodInfo = new ExpressionMethodInformation();
		if (annotation != null) {
			setAnnotatedFields(annotation, methodInfo, method);
			setJoins(annotation, methodInfo, pathBuilder, entityType);
		} else {
			setFields(methodInfo, method);
		}
		methodInfo
				.setMethodParameters(new MethodParameters(method, PARAM_VALUE));
		methodInfo.setMethod(method);
		setStaticFilter(methodInfo,method);
		return methodInfo;
	}

	private static ExpressionMethod getAnnotation(Method m) {
		return m.getAnnotation(ExpressionMethod.class);
	}

	private static void setAnnotatedFields(ExpressionMethod expressionAnn,
			ExpressionMethodInformation methodInfo, Method method) {
		methodInfo.setDefaultedPageable(expressionAnn.defaultedPageable());
		methodInfo
				.setMethodType(expressionAnn.searchMethod() ? MethodType.SEARCH
						: MethodType.FILTER);
		if (!expressionAnn.name().isEmpty())
			methodInfo.setName(expressionAnn.name());
		else
			methodInfo.setName(method.getName());

	}

	private static void setFields(ExpressionMethodInformation methodInfo,
			Method method) {
		methodInfo.setDefaultedPageable(true);
		methodInfo.setMethodType(MethodType.FILTER);
		methodInfo.setName(method.getName());
	}

	private static void setStaticFilter(ExpressionMethodInformation methodInfo,
			Method method) {
		StaticFilter ann = AnnotationUtils.findAnnotation(method,
				StaticFilter.class);
		if (ann != null) {
			orest.expression.registry.ExpressionMethodInformation.StaticFilter staticFilter = new orest.expression.registry.ExpressionMethodInformation.StaticFilter(ann.offOnCondition(), ann.parameters());
			methodInfo.setStaticFilter(staticFilter);
		}

	}

	private static void setJoins(ExpressionMethod expressionAnn,
			ExpressionMethodInformation methodInfo, PathBuilder<?> builder,
			Class<?> entityType) {
		List<ExpressionMethodInformation.Join> joins = new ArrayList<ExpressionMethodInformation.Join>(
				expressionAnn.joins().length);

		for (orest.expression.registry.Join j : expressionAnn.joins()) {
			joins.addAll(ExpressionUtils.getJoins(j.value(), builder,
					entityType, j.fetch()));
			// PropertyPath propertyPath = PropertyPath
			// .from(j.value(), entityType);
			// while (propertyPath != null) {
			// Path<?> path = null;
			// boolean collection = false;
			// Class<?> type;
			// if (propertyPath.isCollection()) {
			// path = builder.getCollection(propertyPath.getSegment(),
			// propertyPath.getType());
			// collection = true;
			// } else{
			// path = builder.get(propertyPath.getSegment(),
			// propertyPath.getType());
			// }
			// joins.add(new ExpressionMethodInformation.Join(path,
			// collection, j.fetch(),propertyPath.getType()));
			// propertyPath = propertyPath.next();
			// }
		}
		methodInfo.setJoins(joins);
	}
}
