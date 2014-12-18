package openrest.security.validator;

import javax.servlet.http.HttpServletRequest;

import openrest.webmvc.ParentAwareObject;

public interface ResourceFilter<T> {

	void validateMainResource(HttpServletRequest request, T resource) throws NotAuthorizedException;

	boolean validateResource(HttpServletRequest request, T resource, ParentAwareObject parent);

	Class<?> supports();

}
