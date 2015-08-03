package orest.dto.authorization;

public interface AuthorizationStrategy<PRINCIPAL, DTO, ENTITY> {
	boolean isAuthorized(PRINCIPAL principal, DTO dto, ENTITY entity);
}
