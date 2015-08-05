package orest.dto.authorization;

public interface DtoAuthorizationStratetyFactory {

	DtoAuthorizationStrategy getAuthorizationStrategy(Class<? extends DtoAuthorizationStrategy> type);
}
