package orest.dto.authorization;

import lombok.NonNull;
import orest.dto.authorization.annotation.AuthStrategies;
import orest.exception.OrestException;
import orest.exception.OrestExceptionDictionary;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthStrategiesAnnotationAuthorizationStrategy implements AuthorizationStrategy<Object, Object, Object>{

	
	private final AuthorizationStratetyFactory strategyFactory;
	
	public AuthStrategiesAnnotationAuthorizationStrategy(@NonNull AuthorizationStratetyFactory strategyFactory) {
		this.strategyFactory = strategyFactory;
	}

	@Override
	public boolean isAuthorized(Object principal, Object dto, Object entity) {
		AuthStrategies authStrategiesAnn = AnnotationUtils.findAnnotation(dto.getClass(), AuthStrategies.class);
		if(authStrategiesAnn == null) return true;
		for(Class<? extends AuthorizationStrategy> strategyType: authStrategiesAnn.value()){
			AuthorizationStrategy strategy = strategyFactory.getAuthorizationStrategy(strategyType);
			if(strategy == null)
				throw new OrestException(OrestExceptionDictionary.NO_SUCH_AUTHORIZATION_STRATEGY, String.format("There is no strategy of type: %s",	 strategyType));
			boolean isAuthorized = strategy.isAuthorized(principal, dto, entity);
			if(!isAuthorized) return false;
		}
		return true;
	}

}
