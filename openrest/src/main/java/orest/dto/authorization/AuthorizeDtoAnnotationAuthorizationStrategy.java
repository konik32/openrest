package orest.dto.authorization;

import java.util.List;

import lombok.NonNull;
import orest.annotation.utils.HierarchicalAnnotationUtils;
import orest.dto.authorization.annotation.AuthorizeDto;
import orest.exception.OrestException;
import orest.exception.OrestExceptionDictionary;

public class AuthorizeDtoAnnotationAuthorizationStrategy implements DtoAuthorizationStrategy<Object, Object, Object> {

	private final DtoAuthorizationStratetyFactory strategyFactory;

	public AuthorizeDtoAnnotationAuthorizationStrategy(@NonNull DtoAuthorizationStratetyFactory strategyFactory) {
		this.strategyFactory = strategyFactory;
	}

	@Override
	public boolean isAuthorized(Object principal, Object dto, Object entity) {
		List<AuthorizeDto> authStrategiesAnns = HierarchicalAnnotationUtils.getAllHierarchicalAnnotations(
				dto.getClass(), AuthorizeDto.class);
		for (AuthorizeDto authStrategiesAnn : authStrategiesAnns) {
			for (Class<? extends DtoAuthorizationStrategy> strategyType : authStrategiesAnn.value()) {
				DtoAuthorizationStrategy strategy = strategyFactory.getAuthorizationStrategy(strategyType);
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
