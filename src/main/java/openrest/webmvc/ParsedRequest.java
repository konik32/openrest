package openrest.webmvc;

import openrest.domain.OpenRestQueryParameterHolder;

import org.springframework.data.mapping.PropertyPath;

/**
 * Wrapper containing information about requested and filtered resource
 * 
 * @author Szymon Konicki
 *
 */

public class ParsedRequest {

	private final OpenRestQueryParameterHolder partTreeSpecification;
	private final Class<?> domainClass;
	private final PropertyPath propertyPath;
	private final String[] dtos;

	public ParsedRequest(Class<?> domainClass, OpenRestQueryParameterHolder partTreeSpecification, String[] dtos) {
		this(domainClass, null, partTreeSpecification, dtos);
	}

	public ParsedRequest(Class<?> domainClass, PropertyPath propertyPath, OpenRestQueryParameterHolder partTreeSpecification, String[] dtos) {
		this.domainClass = domainClass;
		this.partTreeSpecification = partTreeSpecification;
		this.propertyPath = propertyPath;
		this.dtos = dtos;
	}

	public OpenRestQueryParameterHolder getPartTreeSpecification() {
		return partTreeSpecification;
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
