package pl.openrest.dto.security.authorization;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import pl.openrest.core.utils.HierarchicalAnnotationUtils;
import pl.openrest.dto.security.authorization.annotation.AuthorizeDto;
import pl.openrest.exception.OrestException;
import pl.openrest.exception.OrestExceptionDictionary;

public class AuthorizeDtoAnnotationAuthorizationStrategy implements DtoAuthorizationStrategy<Object, Object, Object> {

    private final DtoAuthorizationStrategyFactory strategyFactory;
    private final AccessDecisionManager accessDecisionManager;

    public AuthorizeDtoAnnotationAuthorizationStrategy(@NonNull DtoAuthorizationStrategyFactory strategyFactory,
            @NonNull AccessDecisionManager accessDecisionManager) {
        this.strategyFactory = strategyFactory;
        this.accessDecisionManager = accessDecisionManager;
    }

    public AuthorizeDtoAnnotationAuthorizationStrategy(@NonNull DtoAuthorizationStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
        this.accessDecisionManager = new UnanimousAccessDecisionManger();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public int isAuthorized(Object principal, Object dto, Object entity) {
        List<AuthorizeDto> authStrategiesAnns = HierarchicalAnnotationUtils.getAllHierarchicalAnnotations(dto.getClass(),
                AuthorizeDto.class);
        List<DtoAuthorizationStrategy> strategies = new ArrayList<>();
        for (AuthorizeDto authStrategiesAnn : authStrategiesAnns) {
            for (Class<? extends DtoAuthorizationStrategy> strategyType : authStrategiesAnn.value()) {
                DtoAuthorizationStrategy strategy = strategyFactory.getAuthorizationStrategy(strategyType);
                if (strategy == null)
                    throw new OrestException(OrestExceptionDictionary.NO_SUCH_AUTHORIZATION_STRATEGY, String.format(
                            "There is no strategy of type: %s", strategyType));
                strategies.add(strategy);
            }
        }
        return accessDecisionManager.isAuthorized(principal, dto, entity, strategies);
    }
}
