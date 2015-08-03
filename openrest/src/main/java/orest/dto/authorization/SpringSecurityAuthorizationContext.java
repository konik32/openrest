package orest.dto.authorization;

import lombok.NonNull;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SpringSecurityAuthorizationContext {
	
	private final AuthorizationStrategy authorizationStrategy;

	public SpringSecurityAuthorizationContext(@NonNull AuthorizationStrategy authorizationStrategy) {
		this.authorizationStrategy = authorizationStrategy;
	}
	
	public void invokeStrategy(Object dto, Object entity){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		authorizationStrategy.isAuthorized(authentication.getPrincipal(),dto, entity);
	}
	
	
	

}
