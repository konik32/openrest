package pl.openrest.dto.security.authorization;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;

public class UnanimousAccessDecisionManger extends AccessDecisionManager {

    @Override
    public int isAuthorized(Object principal, Object dto, Object entity, List<DtoAuthorizationStrategy> strategies) {
        int grant = 0;
        for (DtoAuthorizationStrategy strategy : strategies) {
            int result = strategy.isAuthorized(principal, dto, entity);
            switch (result) {
            case DtoAuthorizationStrategy.DENY:
                throw new AccessDeniedException(String.format("Strategy: %s denied access", strategy.getClass().getName()));
            case DtoAuthorizationStrategy.GRANT:
                grant++;
                break;
            default:
                break;
            }
        }
        if (grant > 0)
            return 1;
        allowIfAllAbstainDecisions();
        return 0;
    }

}
