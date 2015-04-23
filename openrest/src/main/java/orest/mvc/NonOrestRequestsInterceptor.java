package orest.mvc;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import orest.exception.OrestException;
import orest.exception.OrestExceptionDictionary;

import org.springframework.data.rest.webmvc.BaseUri;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@RequiredArgsConstructor
public class NonOrestRequestsInterceptor extends HandlerInterceptorAdapter {

	private final BaseUri baseUri;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String lookupPath = baseUri.getRepositoryLookupPath(request.getRequestURI());
		if (StringUtils.isEmpty(lookupPath) || lookupPath.matches(".+\\/search.*"))
			return true;
		String method = request.getMethod();
		if (method.equals("GET")) {
			if (!request.getParameterMap().containsKey("orest"))
				throw new OrestException(OrestExceptionDictionary.NON_OREST_REQUEST,
						"Request should cointain orest parameter");
		} else if (method.equals("POST") || method.equals("PATCH")) {
			if (!request.getParameterMap().containsKey("dto"))
				throw new OrestException(OrestExceptionDictionary.NON_OREST_REQUEST,
						"Request should cointain dto parameter");
		}
		return true;
	}
}
