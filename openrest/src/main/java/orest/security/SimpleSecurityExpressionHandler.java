package orest.security;

import org.springframework.security.access.expression.AbstractSecurityExpressionHandler;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;

public class SimpleSecurityExpressionHandler extends AbstractSecurityExpressionHandler<Object> {

	
	@Override
	protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, Object object) {
		SecurityExpressionRoot root = getRoot(authentication, object);
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setRoleHierarchy(getRoleHierarchy());
		return root;
	}

	protected SecurityExpressionRoot getRoot(Authentication authentication, Object object) {
		return new SimpleSecurityExpressionRoot(authentication);
	}

}
