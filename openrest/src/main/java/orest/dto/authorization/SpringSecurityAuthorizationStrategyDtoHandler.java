package orest.dto.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import lombok.NonNull;
import orest.dto.handler.DtoHandler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityAuthorizationStrategyDtoHandler implements DtoHandler {

	private List<DtoAuthorizationStrategy> authorizationStrategies = new ArrayList<DtoAuthorizationStrategy>();

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
		invokeStrategies(principal, dto, entity);
	}

	private void invokeStrategies(Object principal, Object dto, Object entity) {
		for (DtoAuthorizationStrategy authorizationStrategy : authorizationStrategies) {
			if (!authorizationStrategy.isAuthorized(principal, dto, entity))
				throw new AccessDeniedException("Access is denied");
		}
	}

}
