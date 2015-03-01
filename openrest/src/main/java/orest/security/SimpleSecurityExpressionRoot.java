package orest.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;

public class SimpleSecurityExpressionRoot extends SecurityExpressionRoot {

	public SimpleSecurityExpressionRoot(Authentication authentication) {
		super(authentication);
	}

}
