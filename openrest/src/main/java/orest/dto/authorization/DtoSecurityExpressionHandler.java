package orest.dto.authorization;

import lombok.Getter;
import lombok.ToString;

import org.springframework.security.access.expression.AbstractSecurityExpressionHandler;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;

public class DtoSecurityExpressionHandler extends AbstractSecurityExpressionHandler<DtoSecurityWrapper> {

	@Override
	protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication,
			DtoSecurityWrapper object) {
		SecurityExpressionRoot root = getRoot(authentication, object);
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setRoleHierarchy(getRoleHierarchy());
		return root;
	}

	protected SecurityExpressionRoot getRoot(Authentication authentication, DtoSecurityWrapper object) {
		return new DtoSecurityExpressionRoot(authentication, object);
	}

	@Getter
	@ToString
	private class DtoSecurityExpressionRoot extends SecurityExpressionRoot {

		private final Object dto;
		private final Object entity;

		public DtoSecurityExpressionRoot(Authentication authentication, DtoSecurityWrapper evaluationWrapper) {
			super(authentication);
			this.dto = evaluationWrapper.getDto();
			this.entity = evaluationWrapper.getEntity();
		}

	}

}
