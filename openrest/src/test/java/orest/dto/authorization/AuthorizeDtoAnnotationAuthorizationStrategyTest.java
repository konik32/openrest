package orest.dto.authorization;

import static org.junit.Assert.*;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import orest.dto.authorization.annotation.AuthorizeDto;
import orest.exception.OrestException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizeDtoAnnotationAuthorizationStrategyTest {

	private AuthorizeDtoAnnotationAuthorizationStrategy authorizationStrategy;

	@Mock
	private DtoAuthorizationStratetyFactory strategyFactory;

	private UserDetails principal;
	private Object dto;
	private Object entity;

	@Before
	public void setUp() {
		strategyFactory = mock(DtoAuthorizationStratetyFactory.class);
		principal = mock(UserDetails.class);
		dto = mock(TestDto.class);
		entity = mock(Object.class);
		authorizationStrategy = new AuthorizeDtoAnnotationAuthorizationStrategy(strategyFactory);
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
		DtoAuthorizationStrategy strategy = mock(DtoAuthorizationStrategy.class);
		when(strategy.isAuthorized(principal, dto, entity)).thenReturn(false);
		when(strategyFactory.getAuthorizationStrategy(DtoAuthorizationStrategy.class)).thenReturn(strategy);
		when(strategyFactory.getAuthorizationStrategy(TestStrategy.class)).thenReturn(new TestStrategy());
		// when

		// then
		assertFalse(authorizationStrategy.isAuthorized(principal, dto, entity));
	}

	@Test(expected = OrestException.class)
	public void shouldThrowOrestException() throws Exception {
		// given
		DtoAuthorizationStrategy strategy = mock(DtoAuthorizationStrategy.class);
		when(strategy.isAuthorized(principal, dto, entity)).thenReturn(false);
		// when

		// then
		assertFalse(authorizationStrategy.isAuthorized(principal, dto, entity));
	}

	@Test
	public void shouldCallParentAuthorizationStrategy() throws Exception {
		// given
		dto = mock(ChildTestDto.class);
		DtoAuthorizationStrategy strategy = mock(DtoAuthorizationStrategy.class);
		when(strategy.isAuthorized(principal, dto, entity)).thenReturn(false);
		when(strategyFactory.getAuthorizationStrategy(DtoAuthorizationStrategy.class)).thenReturn(strategy);
		when(strategyFactory.getAuthorizationStrategy(TestStrategy.class)).thenReturn(new TestStrategy());
		// when
		authorizationStrategy.isAuthorized(principal, dto, entity);
		// then
		verify(strategy, times(1)).isAuthorized(principal, dto, entity);
	}

	class TestStrategy implements DtoAuthorizationStrategy<UserDetails, Object, Object> {

		@Override
		public boolean isAuthorized(UserDetails principal, Object dto, Object entity) {
			return true;
		}

	}

	@AuthorizeDto({ TestStrategy.class, DtoAuthorizationStrategy.class })
	class TestDto {

	}

	@AuthorizeDto({ TestStrategy.class })
	class ChildTestDto extends ParentTestStrategy {

	}

	@AuthorizeDto({ DtoAuthorizationStrategy.class })
	class ParentTestStrategy {

	}
}
