package pl.openrest.dto.security.authorization;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import pl.openrest.dto.handler.BeforeCreateMappingHandler;
import pl.openrest.dto.handler.BeforeUpdateMappingHandler;

@SuppressWarnings("rawtypes")
public class DtoAuthorizationStrategyMappingHandler implements BeforeCreateMappingHandler, BeforeUpdateMappingHandler {

    private List<DtoAuthorizationStrategy> authorizationStrategies = new ArrayList<DtoAuthorizationStrategy>();

    private @Setter AccessDecisionManager accessDecisionManager;

    public DtoAuthorizationStrategyMappingHandler(AccessDecisionManager accessDecisionManager) {
        this.accessDecisionManager = accessDecisionManager;
    }

    public DtoAuthorizationStrategyMappingHandler() {
        this(new UnanimousAccessDecisionManger());
    }

    public void addStrategy(DtoAuthorizationStrategy strategy) {
        authorizationStrategies.add(strategy);
    }

    @Override
    public void handle(Object dto) {
        handle(dto, null);
    }

    @Override
    public void handle(Object dto, Object entity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication == null ? null : authentication.getPrincipal();
        accessDecisionManager.isAuthorized(principal, dto, entity, authorizationStrategies);
    }
}
