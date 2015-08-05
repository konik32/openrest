package orest.dto.authorization;

import java.util.List;

import orest.annotation.utils.HierarchicalAnnotationUtils;
import orest.authorization.annotation.Secure;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecureAnnotationAuthorizationStrategy implements DtoAuthorizationStrategy<Object, Object, Object> {

	private final DtoSecurityExpressionHandler expressionHandler = new DtoSecurityExpressionHandler();

	@Override
	public boolean isAuthorized(Object principal, Object dto, Object entity) {
		List<Secure> secureAnns = HierarchicalAnnotationUtils.getAllHierarchicalAnnotations(dto.getClass(),
				Secure.class);
		EvaluationContext evaluationContext = null;
		if (!secureAnns.isEmpty())
			evaluationContext = expressionHandler.createEvaluationContext(SecurityContextHolder.getContext()
					.getAuthentication(), new DtoSecurityWrapper(dto, entity));
		for (Secure secureAnn : secureAnns) {
			String condition = secureAnn.value();
			if (!checkCondition(condition, evaluationContext))
				return false;
		}
		return true;
	}

	private boolean checkCondition(String condition, EvaluationContext evaluationContext) {
		if (condition == null || condition.isEmpty())
			return false;
		Expression expr = expressionHandler.getExpressionParser().parseExpression(condition);
		return ExpressionUtils.evaluateAsBoolean(expr, evaluationContext);
	}

}
