package orest.dto.authorization;

public interface AuthorizationStratetyFactory {

	AuthorizationStrategy getAuthorizationStrategy(Class<? extends AuthorizationStrategy> type);
}
