package orest.dto.authorization;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

public class AuthorizationStrategyAgregator implements AuthorizationStrategy<Object, Object, Object> {

	private List<AuthorizationStrategy> authorizationStrategies = new ArrayList<AuthorizationStrategy>();
	
	public void addStrategy(AuthorizationStrategy strategy){
		authorizationStrategies.add(strategy);
	}
	
	@Override
	public boolean isAuthorized(Object principal, Object dto, Object entity) {
		for(AuthorizationStrategy strategy: authorizationStrategies){
			boolean isAuthorized = strategy.isAuthorized(principal, dto, entity);
			if(!isAuthorized) return false;
		}
		return true;
	}

}
