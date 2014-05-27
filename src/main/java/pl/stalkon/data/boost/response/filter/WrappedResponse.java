package pl.stalkon.data.boost.response.filter;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import pl.stalkon.data.boost.response.Response;

public class WrappedResponse {

	private final HttpServletRequest request;
	private final Principal principal;
	private final Response response;
	
	public WrappedResponse(HttpServletRequest request, Principal principal,
			Response response) {
		this.request = request;
		this.principal = principal;
		this.response = response;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public Response getResponse() {
		return response;
	}
}
