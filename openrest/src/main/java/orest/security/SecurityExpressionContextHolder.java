package orest.security;

import org.springframework.expression.EvaluationContext;

public interface SecurityExpressionContextHolder {

	EvaluationContext getEvaluationContext();
}
