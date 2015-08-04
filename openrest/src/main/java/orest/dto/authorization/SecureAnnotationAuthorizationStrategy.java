package orest.dto.authorization;

import java.util.List;

import lombok.NonNull;
import orest.annotation.utils.HierarchicalAnnotationUtils;
import orest.dto.authorization.annotation.AuthStrategies;
import orest.dto.authorization.annotation.Secure;
import orest.security.ExpressionEvaluator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class SecureAnnotationAuthorizationStrategy implements AuthorizationStrategy<Object, Object, Object> {

	private final ExpressionEvaluator expressionEvaluator;

	@Autowired
	public SecureAnnotationAuthorizationStrategy(@NonNull ExpressionEvaluator expressionEvaluator) {
		this.expressionEvaluator = expressionEvaluator;
	}

	@Override
	public boolean isAuthorized(Object principal, Object dto, Object entity) {
		List<Secure> secureAnns = HierarchicalAnnotationUtils.getAllHierarchicalAnnotations(
				dto.getClass(), Secure.class);
		for (Secure secureAnn : secureAnns) {
			String condition = secureAnn.value();
			if(!expressionEvaluator.checkCondition(condition))
				return false;
		}
		return true;
	}

}
