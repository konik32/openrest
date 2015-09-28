package pl.openrest.dto.security.authorization;

public interface DtoAuthorizationStrategy<PRINCIPAL, DTO, ENTITY> {
    int isAuthorized(PRINCIPAL principal, DTO dto, ENTITY entity);

    public static final int GRANT = 1;
    public static final int ABSTAIN = 0;
    public static final int DENY = -1;
}
