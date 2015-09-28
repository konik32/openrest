package pl.openrest.dto.security.authorization;

import java.util.List;

import lombok.Setter;

import org.springframework.security.access.AccessDeniedException;

public abstract class AccessDecisionManager {

    private @Setter boolean allowIfAllAbstainDecisions = true;

    public abstract int isAuthorized(Object principal, Object dto, Object entity, List<DtoAuthorizationStrategy> strategies);

    public void allowIfAllAbstainDecisions() {
        if (!allowIfAllAbstainDecisions)
            throw new AccessDeniedException("Access denied. All strategies abstained");
    }
}
