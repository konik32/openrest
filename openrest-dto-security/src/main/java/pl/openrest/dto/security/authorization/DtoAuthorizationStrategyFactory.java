package pl.openrest.dto.security.authorization;

@SuppressWarnings("rawtypes")
public interface DtoAuthorizationStrategyFactory {

    DtoAuthorizationStrategy getAuthorizationStrategy(Class<? extends DtoAuthorizationStrategy> type);
}
