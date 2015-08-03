package orest.dto.authorization;

import lombok.NonNull;
import orest.dto.authorization.annotation.Secure;
import orest.security.ExpressionEvaluator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class SecureAnnotationAuthorizationStrategy implements AuthorizationStrategy<UserDetails, Object, Object> {

	private final ExpressionEvaluator expressionEvaluator;

	@Autowired
	public SecureAnnotationAuthorizationStrategy(@NonNull ExpressionEvaluator expressionEvaluator) {
		this.expressionEvaluator = expressionEvaluator;
	}

	@Override
	public boolean isAuthorized(UserDetails principal, Object dto, Object entity) {
		Secure secureAnn = AnnotationUtils.findAnnotation(dto.getClass(), Secure.class);
		if (secureAnn == null)
			return true;
		String condition = secureAnn.value();
		return expressionEvaluator.checkCondition(condition);
	}

}
