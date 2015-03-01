package orest.security;

import lombok.RequiredArgsConstructor;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;

@RequiredArgsConstructor
public class ExpressionEvaluator {

	private final ExpressionParser expressionParser;
	private final SecurityExpressionContextHolder contextHolder;

	public Object processParameter(String param, Class<?> type) {
		return expressionParser.parseExpression(param).getValue(
				contextHolder.getEvaluationContext(), type);
	}

	public boolean checkCondition(String condition) {
		if (condition == null || condition.isEmpty())
			return false;
		Expression expr = expressionParser.parseExpression(condition);
		return org.springframework.security.access.expression.ExpressionUtils
				.evaluateAsBoolean(expr, contextHolder.getEvaluationContext());
	}
}
