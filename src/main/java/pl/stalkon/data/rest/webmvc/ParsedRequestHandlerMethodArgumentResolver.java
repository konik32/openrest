package pl.stalkon.data.rest.webmvc;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ParsedRequestHandlerMethodArgumentResolver implements
		HandlerMethodArgumentResolver {

	private ParsedRequestFactory partTreeSpecificationFactory;
	private final ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver;

	public ParsedRequestHandlerMethodArgumentResolver(
			ParsedRequestFactory partTreeSpecificationFactory,
			ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver) {
		this.partTreeSpecificationFactory = partTreeSpecificationFactory;
		this.resourceMetadataResolver = resourceMetadataResolver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.method.support.HandlerMethodArgumentResolver#
	 * supportsParameter(org.springframework.core.MethodParameter)
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return ParsedRequest.class.isAssignableFrom(parameter
				.getParameterType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.method.support.HandlerMethodArgumentResolver#
	 * resolveArgument(org.springframework.core.MethodParameter,
	 * org.springframework.web.method.support.ModelAndViewContainer,
	 * org.springframework.web.context.request.NativeWebRequest,
	 * org.springframework.web.bind.support.WebDataBinderFactory)
	 */
	@Override
	public ParsedRequest resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {

		String filter = webRequest.getParameter("filter");
		String subject = webRequest.getParameter("subject");
		String view = webRequest.getParameter("expand");

		ResourceMetadata metadata = resourceMetadataResolver.resolveArgument(
				parameter, mavContainer, webRequest, binderFactory);

		String path = webRequest.getNativeRequest(HttpServletRequest.class).getRequestURI();
		// ParsedQueryParameters parsedParameters =
		// parsers.parseQueryParameters(
		// metadata.getDomainType(), filter, subject,view);

		return partTreeSpecificationFactory.getSpecificationInformation(filter, view,
				subject, path, metadata.getDomainType());
	}

}
