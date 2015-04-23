package orest.security;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;

@RequiredArgsConstructor
public class RequestScopedExpressionEvaluator implements ExpressionEvaluator {
	
	@Autowired
	private SimpleSecurityExpressionHandler expressionHandler;
	
	@Autowired
	private  SecurityExpressionContextHolder contextHolder;

	public Object processParameter(String param, Class<?> type) {
		return expressionHandler.getExpressionParser().parseExpression(param).getValue(
				contextHolder.getEvaluationContext(), type);
	}

	public boolean checkCondition(String condition) {
		if (condition == null || condition.isEmpty())
			return false;
		Expression expr = expressionHandler.getExpressionParser().parseExpression(condition);
		return org.springframework.security.access.expression.ExpressionUtils
				.evaluateAsBoolean(expr, contextHolder.getEvaluationContext());
	}

}
