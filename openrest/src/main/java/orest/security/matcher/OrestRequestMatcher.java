package orest.security.matcher;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import lombok.Setter;

import org.springframework.data.rest.webmvc.BaseUri;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Setter
public class OrestRequestMatcher implements RequestMatcher {

	private final BaseUri baseUri;
	private final Pattern requestPattern;
	private final String method;
	private String dto;
	private String projection;

	public OrestRequestMatcher(BaseUri baseUri, Pattern requestPattern, String method) {
		this.baseUri = baseUri;
		this.requestPattern = requestPattern;
		this.method = method;
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		String lookupPath = baseUri.getRepositoryLookupPath(request.getRequestURI());
		if (lookupPath == null)
			return true;
		boolean matches = method.equals(request.getMethod());
		if (dto != null)
			matches = matches && dto.equals(request.getParameter("dto"));
		if (projection != null)
			matches = matches && projection.equals(request.getParameter("projection"));
		return matches && requestPattern.matcher(lookupPath).matches();
	}

}
