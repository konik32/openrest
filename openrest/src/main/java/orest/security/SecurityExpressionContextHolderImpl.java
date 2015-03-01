package orest.security;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityExpressionContextHolderImpl implements SecurityExpressionContextHolder,InitializingBean {

	private EvaluationContext evaluationContext;
	private final SimpleSecurityExpressionHandler expressionHandler;
	
	public SecurityExpressionContextHolderImpl(
			SimpleSecurityExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		evaluationContext = expressionHandler.createEvaluationContext(
				SecurityContextHolder.getContext().getAuthentication(), null);
	}

	public EvaluationContext getEvaluationContext() {
		return evaluationContext;
	}
}
