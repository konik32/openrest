package openrest.webmvc;

import static org.springframework.hateoas.TemplateVariable.VariableType.REQUEST_PARAM;
import static org.springframework.hateoas.TemplateVariable.VariableType.REQUEST_PARAM_CONTINUED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.TemplateVariable.VariableType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.UriComponents;

public class ParsedRequestHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private ParsedRequestFactory partTreeSpecificationFactory;
	private final ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver;
	private final PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver;
	private RepositoryRestConfiguration config;
	
	private static final String FILTER_PARAM_NAME = "filter";
	private static final String EXPAND_PARAM_NAME = "expand";
	private static final String DISTINCT_PARAM_NAME = "distinct";
	private static final String COUNT_PARAM_NAME = "count";
	private static final String STATIC_FILTER_PARAM_NAME = "sFilter";
	private static final String OREST_PARAM_NAME = "orest";
	
	public ParsedRequestHandlerMethodArgumentResolver(ParsedRequestFactory partTreeSpecificationFactory,
			ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver,PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver, RepositoryRestConfiguration config) {
		this.partTreeSpecificationFactory = partTreeSpecificationFactory;
		this.resourceMetadataResolver = resourceMetadataResolver;
		this.pageableHandlerMethodArgumentResolver = pageableHandlerMethodArgumentResolver;
		this.config = config;
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
		return ParsedRequest.class.isAssignableFrom(parameter.getParameterType());
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
	public ParsedRequest resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {

		String filter = webRequest.getParameter(FILTER_PARAM_NAME);
		String distinct = webRequest.getParameter(DISTINCT_PARAM_NAME);
		String count = webRequest.getParameter(COUNT_PARAM_NAME);
		String expand = webRequest.getParameter(EXPAND_PARAM_NAME);
		String sFilter = webRequest.getParameter(STATIC_FILTER_PARAM_NAME);

		ResourceMetadata metadata = resourceMetadataResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
		Pageable pageable = pageableHandlerMethodArgumentResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
		
		String path = webRequest.getNativeRequest(HttpServletRequest.class).getRequestURI();
		if (path.startsWith(config.getBaseUri().getPath())) {
			path = path.replace(config.getBaseUri().getPath(), "");
		}

		// ParsedQueryParameters parsedParameters =
		// parsers.parseQueryParameters(
		// metadata.getDomainType(), filter, subject,view);

		return partTreeSpecificationFactory.getParsedRequest(filter, expand, distinct,count, path, sFilter, metadata.getDomainType(), pageable);
	}

	public TemplateVariables getTemplateVariables(UriComponents template, boolean isCollection) {
		List<TemplateVariable> names = new ArrayList<TemplateVariable>();
		MultiValueMap<String, String> queryParameters = template.getQueryParams();
		boolean append = !queryParameters.isEmpty();
		List<String> propertyNames = new ArrayList<String>(Arrays.asList(OREST_PARAM_NAME,EXPAND_PARAM_NAME, STATIC_FILTER_PARAM_NAME,DISTINCT_PARAM_NAME));
		if(isCollection){
			propertyNames.add(FILTER_PARAM_NAME);
			propertyNames.add(COUNT_PARAM_NAME);
		}
		for (String propertyName : propertyNames) {
			if (!queryParameters.containsKey(propertyName)) {
				VariableType type = append ? REQUEST_PARAM_CONTINUED : REQUEST_PARAM;
				names.add(new TemplateVariable(propertyName, type));
			}
		}
		return new TemplateVariables(names);
	}

}
