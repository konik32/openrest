package pl.openrest.dto.security.authorization;

public interface DtoAuthorizationStrategy<P, D, E> {

    public static final int GRANT = 1;
    public static final int ABSTAIN = 0;
    public static final int DENY = -1;

    int isAuthorized(P principal, D dto, E entity);

}
