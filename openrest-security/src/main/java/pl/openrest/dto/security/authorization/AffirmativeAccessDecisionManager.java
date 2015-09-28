package pl.openrest.dto.security.authorization;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;

public class AffirmativeAccessDecisionManager extends AccessDecisionManager {

    @Override
    public int isAuthorized(Object principal, Object dto, Object entity, List<DtoAuthorizationStrategy> strategies) {
        int deny = 0;
        for (DtoAuthorizationStrategy strategy : strategies) {
            int result = strategy.isAuthorized(principal, dto, entity);
            switch (result) {
            case DtoAuthorizationStrategy.DENY:
                deny++;
                break;
            case DtoAuthorizationStrategy.GRANT:
                return 1;
            default:
                break;
            }
        }
        if (deny > 0)
            throw new AccessDeniedException("Access denied");
        allowIfAllAbstainDecisions();
        return 0;
    }

}
