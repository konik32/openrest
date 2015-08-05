package orest.dto.authorization;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

@RunWith(MockitoJUnitRunner.class)
public class SpringSecurityAuthorizationStrategyDtoHandlerTest {

	
	private SpringSecurityAuthorizationStrategyDtoHandler handler;
	
	@Before
	public void setUp(){
		handler = new SpringSecurityAuthorizationStrategyDtoHandler();
	}
	
	@Test
	public void shouldNotThrowExceptionOnNoStrategies() throws Exception {
		handler.handle(mock(Object.class),  mock(Object.class));
	}
	
	
	@Test(expected=AccessDeniedException.class)
	public void shouldThrowAccessDeniedExceptionOnAnyUnauthorizedStrategy() throws Exception {
		// given
		DtoAuthorizationStrategy strategy1 = mock(DtoAuthorizationStrategy.class);
		DtoAuthorizationStrategy strategy2 = mock(DtoAuthorizationStrategy.class);
		when(strategy1.isAuthorized(mock(UserDetails.class), mock(Object.class),  mock(Object.class))).thenReturn(true);
		when(strategy2.isAuthorized(mock(UserDetails.class), mock(Object.class),  mock(Object.class))).thenReturn(false);
		handler.addStrategy(strategy1);
		handler.addStrategy(strategy2);
		// when

		// then
		handler.handle(mock(Object.class),  mock(Object.class));
	}
}
