package orest.dto.authorization;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;

@RunWith(MockitoJUnitRunner.class)
public class AuthrizationStrategyAggregatorTest {

	
	private AuthrizationStrategyAggregator aggregator;
	
	@Before
	public void setUp(){
		aggregator = new AuthrizationStrategyAggregator();
	}
	
	@Test
	public void shouldReturnTrueOnNoStrategies() throws Exception {
		assertTrue(aggregator.isAuthorized(mock(UserDetails.class), mock(Object.class),  mock(Object.class)));
	}
	
	
	@Test
	public void shouldReturnFalseOnAnyUnauthorizedStrategy() throws Exception {
		// given
		AuthorizationStrategy strategy1 = mock(AuthorizationStrategy.class);
		AuthorizationStrategy strategy2 = mock(AuthorizationStrategy.class);
		when(strategy1.isAuthorized(mock(UserDetails.class), mock(Object.class),  mock(Object.class))).thenReturn(true);
		when(strategy2.isAuthorized(mock(UserDetails.class), mock(Object.class),  mock(Object.class))).thenReturn(false);
		aggregator.addStrategy(strategy1);
		aggregator.addStrategy(strategy2);
		// when

		// then
		assertFalse(aggregator.isAuthorized(mock(UserDetails.class), mock(Object.class),  mock(Object.class)));
	}
}
