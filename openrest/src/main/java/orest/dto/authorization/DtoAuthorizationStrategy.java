package orest.dto.authorization;

public interface DtoAuthorizationStrategy<PRINCIPAL, DTO, ENTITY> {
	boolean isAuthorized(PRINCIPAL principal, DTO dto, ENTITY entity);
}
