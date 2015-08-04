package orest.dto.authorization;

import java.util.List;

import lombok.NonNull;
import orest.annotation.utils.HierarchicalAnnotationUtils;
import orest.dto.authorization.annotation.AuthStrategies;
import orest.exception.OrestException;
import orest.exception.OrestExceptionDictionary;

public class AuthStrategiesAnnotationAuthorizationStrategy implements AuthorizationStrategy<Object, Object, Object> {

	private final AuthorizationStratetyFactory strategyFactory;

	public AuthStrategiesAnnotationAuthorizationStrategy(@NonNull AuthorizationStratetyFactory strategyFactory) {
		this.strategyFactory = strategyFactory;
	}

	@Override
	public boolean isAuthorized(Object principal, Object dto, Object entity) {
		List<AuthStrategies> authStrategiesAnns = HierarchicalAnnotationUtils.getAllHierarchicalAnnotations(
				dto.getClass(), AuthStrategies.class);
		for (AuthStrategies authStrategiesAnn : authStrategiesAnns) {
			for (Class<? extends AuthorizationStrategy> strategyType : authStrategiesAnn.value()) {
				AuthorizationStrategy strategy = strategyFactory.getAuthorizationStrategy(strategyType);
				if (strategy == null)
					throw new OrestException(OrestExceptionDictionary.NO_SUCH_AUTHORIZATION_STRATEGY, String.format(
							"There is no strategy of type: %s", strategyType));
				boolean isAuthorized = strategy.isAuthorized(principal, dto, entity);
				if (!isAuthorized)
					return false;
			}
		}
		return true;
	}

}
