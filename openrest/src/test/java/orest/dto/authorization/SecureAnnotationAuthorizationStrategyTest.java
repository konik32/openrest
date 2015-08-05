package orest.dto.authorization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import orest.authorization.annotation.Secure;
import orest.security.ExpressionEvaluator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

		Authentication auth = new UsernamePasswordAuthenticationToken(mock(UserDetails.class), null);

		SecurityContextHolder.getContext().setAuthentication(auth);
		authorizationStrategy = new SecureAnnotationAuthorizationStrategy();

	}

	@Test
	public void shouldReturnFalseOnIsAhutorized() throws Exception {
		assertFalse(authorizationStrategy.isAuthorized(principal, dto, entity));
	}

	@Test
	public void shouldReturnTrueOnNoSecureAnnotation() throws Exception {
		assertTrue(authorizationStrategy.isAuthorized(principal, mock(Object.class), entity));
	}

	@Test
	public void shouldReturnFalseOnParentIsNotAhutorized() throws Exception {
		assertFalse(authorizationStrategy.isAuthorized(principal, mock(ChildTestDto.class), entity));
	}

	@Secure("dto.equals(entity)")
	public static class TestDto {

	}

	@Secure("dto.equals(dto)")
	class ChildTestDto extends ParentTestStrategy {

	}

	@Secure("dto.equals(entity)")
	class ParentTestStrategy {

	}

}
