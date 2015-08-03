package orest.dto.authorization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import orest.dto.authorization.annotation.AuthStrategies;
import orest.exception.OrestException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;

@RunWith(MockitoJUnitRunner.class)
public class AuthStrategiesAnnotationAuthorizationStrategyTest {

	private AuthStrategiesAnnotationAuthorizationStrategy authorizationStrategy;

	@Mock
	private AuthorizationStratetyFactory strategyFactory;

	private UserDetails principal;
	private Object dto;
	private Object entity;

	@Before
	public void setUp() {
		strategyFactory = mock(AuthorizationStratetyFactory.class);
		principal = mock(UserDetails.class);
		dto = mock(TestDto.class);
		entity = mock(Object.class);
		authorizationStrategy = new AuthStrategiesAnnotationAuthorizationStrategy(strategyFactory);
	}

	@Test
	public void shouldReturnTrueOnMissingAuthStrategiesAnn() throws Exception {
		// given

		// when
		assertTrue(authorizationStrategy.isAuthorized(principal, mock(Object.class), entity));
		// then
	}

	@Test
	public void shouldReturnFalseOnNotAuthorizedStrategy() throws Exception {
		// given
		AuthorizationStrategy strategy = mock(AuthorizationStrategy.class);
		when(strategy.isAuthorized(principal, dto, entity)).thenReturn(false);
		when(strategyFactory.getAuthorizationStrategy(AuthorizationStrategy.class)).thenReturn(strategy);
		when(strategyFactory.getAuthorizationStrategy(TestStrategy.class)).thenReturn(new TestStrategy());
		// when

		// then
		assertFalse(authorizationStrategy.isAuthorized(principal, dto, entity));
	}

	@Test(expected = OrestException.class)
	public void shouldThrowOrestException() throws Exception {
		// given
		AuthorizationStrategy strategy = mock(AuthorizationStrategy.class);
		when(strategy.isAuthorized(principal, dto, entity)).thenReturn(false);
		// when

		// then
		assertFalse(authorizationStrategy.isAuthorized(principal, dto, entity));
	}

	class TestStrategy implements AuthorizationStrategy<UserDetails, Object, Object> {

		@Override
		public boolean isAuthorized(UserDetails principal, Object dto, Object entity) {
			return true;
		}

	}

	@AuthStrategies({ TestStrategy.class, AuthorizationStrategy.class })
	class TestDto {

	}
}
