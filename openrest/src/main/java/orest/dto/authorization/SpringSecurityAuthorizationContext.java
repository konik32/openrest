package orest.dto.authorization;

import lombok.NonNull;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityAuthorizationContext {

	private final AuthorizationStrategy authorizationStrategy;

	public SpringSecurityAuthorizationContext(@NonNull AuthorizationStrategy authorizationStrategy) {
		this.authorizationStrategy = authorizationStrategy;
	}

	public void invokeStrategy(Object dto, Object entity) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication == null ? null : authentication.getPrincipal();
		if (!authorizationStrategy.isAuthorized(principal, dto, entity))
			throw new AccessDeniedException("One of defined authorization strategies returned false");
	}

}
