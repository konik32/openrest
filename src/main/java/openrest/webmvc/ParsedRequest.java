package openrest.webmvc;

import openrest.query.parameter.QueryParameterHolder;

import org.springframework.data.mapping.PropertyPath;

/**
 * Wrapper containing information about requested and filtered resource
 * 
 * @author Szymon Konicki
 *
 */

public class ParsedRequest {

	private final QueryParameterHolder queryParameterHolder;
	private final Class<?> domainClass;
	private final PropertyPath propertyPath;
	private final String[] dtos;

	public ParsedRequest(Class<?> domainClass, QueryParameterHolder partTreeSpecification, String[] dtos) {
		this(domainClass, null, partTreeSpecification, dtos);
	}

	public ParsedRequest(Class<?> domainClass, PropertyPath propertyPath, QueryParameterHolder queryParameterHolder, String[] dtos) {
		this.domainClass = domainClass;
		this.queryParameterHolder = queryParameterHolder;
		this.propertyPath = propertyPath;
		this.dtos = dtos;
	}

	public QueryParameterHolder getQueryParameterHolder() {
		return queryParameterHolder;
	}

	public Class<?> getDomainClass() {
		return domainClass;
	}

	public PropertyPath getPropertyPath() {
		return propertyPath;
	}

	public String[] getDtos() {
		return dtos;
	}
}
