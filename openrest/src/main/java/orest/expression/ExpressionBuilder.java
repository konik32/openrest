package orest.expression;

import java.lang.reflect.Method;
import java.util.List;

import lombok.RequiredArgsConstructor;
import orest.expression.registry.ExpressionEntityInformation;
import orest.expression.registry.ExpressionMethodInformation;
import orest.parser.FilterPart;
import orest.parser.FilterPart.FilterPartType;
import orest.repository.ExpressionUtils;
import orest.repository.PredicateContext;
import orest.security.ExpressionEvaluator;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.rest.webmvc.support.Projector;
import org.springframework.hateoas.core.MethodParameters;
import org.springframework.util.ReflectionUtils;

import com.mysema.query.types.expr.BooleanExpression;

@RequiredArgsConstructor
public class ExpressionBuilder {

	private final ConversionService defaultConversionService;
	private final ExpressionEvaluator expressionEvaluator;

	public BooleanExpression createIdEqualsExpression(String value,
			ExpressionEntityInformation expressionEntityInformation) {
		if (value == null)
			return null;
		PersistentEntity<?, ?> pe = expressionEntityInformation
				.getPersistentEntity();
		PersistentProperty<?> idProperty = pe.getIdProperty();
		Object idValue = defaultConversionService.convert(value,
				idProperty.getActualType());
		return ExpressionUtils
				.getPathBuilder(expressionEntityInformation.getEntityType())
				.get(idProperty.getName()).eq(idValue);
	}

	public BooleanExpression createStaticFiltersExpression(
			PredicateContext predicateContext,
			ExpressionEntityInformation expressionEntityInformation) {
		List<ExpressionMethodInformation> staticFitlersMethods = expressionEntityInformation
				.getMethodRegistry().getStaticFilters();
		BooleanExpression exp = null;
		for (ExpressionMethodInformation mInfo : staticFitlersMethods) {
			if (expressionEvaluator.checkCondition(mInfo.getStaticFilter()
					.getCondition()))
				continue;
			BooleanExpression staticExp = getExpression(mInfo.getMethod(),
					mInfo.getStaticFilter().getParameters(),
					expressionEntityInformation.getExpressionRepository(), true);
			predicateContext.addJoins(mInfo.getJoins());
			exp = exp == null ? staticExp : exp.and(staticExp);
		}
		return exp;
	}

	public BooleanExpression create(FilterPart tree,
			PredicateContext predicateContext, Object expressionRepository) {
		if(tree == null) return null;
		return processTreeRecursively(tree, predicateContext,
				expressionRepository);
	}

	public BooleanExpression create(
			ExpressionMethodInformation expressionMethodInformation,
			PredicateContext predicateContext,
			ExpressionEntityInformation expressionEntityInformation,
			String[] parameters) {
		if(expressionMethodInformation == null) return null;
		BooleanExpression exp =  getExpression(expressionMethodInformation.getMethod(),
				parameters,
				expressionEntityInformation.getExpressionRepository(), false);
		predicateContext.addJoins(expressionMethodInformation.getJoins());
		return exp;
	}

	public BooleanExpression processTreeRecursively(FilterPart part,
			PredicateContext predicateContext, Object expressionRepository) {
		if (part.getType() == FilterPartType.LEAF) {
			ExpressionMethodInformation methodInfo = part.getMethodInfo();
			predicateContext.addJoins(methodInfo.getJoins());
			return getExpression(methodInfo.getMethod(), part.getParameters(),
					expressionRepository, false);
		} else {
			BooleanExpression exp = null;
			for (FilterPart p : part.getParts()) {
				BooleanExpression pExp = processTreeRecursively(p,
						predicateContext, expressionRepository);
				exp = exp == null ? pExp
						: part.getType() == FilterPartType.OR ? exp.or(pExp)
								: exp.and(pExp);
			}
			return exp;
		}
	}
	
	public void addExpandJoins(PredicateContext predicateContext, String expand, Class<?> entityType) {
		if (expand == null || expand.isEmpty())
			return;
		String parts[] = expand.split(",");
		for (String part : parts) {
			predicateContext.addJoins(ExpressionUtils.getJoins(part, ExpressionUtils.getPathBuilder(entityType), entityType, true));
		}
	}

	private BooleanExpression getExpression(Method method, String[] params,
			Object expressionRepository, boolean processParams) {
		Object[] parameters = prepareParameters(method, params, processParams);
		return invokeMethod(method, parameters, expressionRepository);
	}

	public Object[] prepareParameters(Method method, String[] rawParameters,
			boolean processParameter) {

		if (rawParameters == null)
			return new Object[0];
		List<MethodParameter> parameters = new MethodParameters(method)
				.getParameters();

		if (parameters.isEmpty()) {
			return new Object[0];
		}

		Object[] result = new Object[parameters.size()];

		for (int i = 0; i < rawParameters.length; i++) {

			MethodParameter param = parameters.get(i);
			String parameterValue = rawParameters[i];
			if (!processParameter)
				result[i] = defaultConversionService.convert(parameterValue,
						TypeDescriptor.forObject(parameterValue),
						new TypeDescriptor(param));
			else
				result[i] = expressionEvaluator.processParameter(
						parameterValue, param.getParameterType());
		}

		return result;
	}

	private BooleanExpression invokeMethod(Method method, Object[] params,
			Object repository) {
		ReflectionUtils.makeAccessible(method);
		return (BooleanExpression) ReflectionUtils.invokeMethod(method,
				repository, params);
	}
}
