package orest.dto.authorization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import orest.dto.authorization.annotation.Secure;
import orest.security.ExpressionEvaluator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;

@RunWith(MockitoJUnitRunner.class)
public class SecureAnnotationAuthorizationStrategyTest {
	@Mock
	private SecureAnnotationAuthorizationStrategy authorizationStrategy;

	@Mock
	private ExpressionEvaluator expressionEvaluator;
	private UserDetails principal;
	private Object dto;
	private Object entity;

	@Before
	public void setUp() {
		expressionEvaluator = mock(ExpressionEvaluator.class);
		
		principal = mock(UserDetails.class);
		dto = mock(TestDto.class);
		entity = mock(Object.class);

		authorizationStrategy = new SecureAnnotationAuthorizationStrategy(expressionEvaluator);

	}

	@Test
	public void shouldReturnFalseOnIsAhutorized() throws Exception {
		// given
		when(expressionEvaluator.checkCondition(anyString())).thenReturn(false);
		// when

		// then
		assertFalse(authorizationStrategy.isAuthorized(principal, dto, entity));
	}
	
	@Test
	public void shouldReturnTrueOnNoSecureAnnotation() throws Exception {
		// given
		// when

		// then
		assertTrue(authorizationStrategy.isAuthorized(principal, mock(Object.class), entity));
	}

	@Secure("hasRole('ADMIN')")
	class TestDto {

	}

}
