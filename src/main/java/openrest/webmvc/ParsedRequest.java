package openrest.webmvc;

import openrest.domain.OpenRestQueryParameterHolder;

import org.springframework.data.mapping.PropertyPath;
/**
 * Wrapper containing information about requested and filtered resource
 * @author Szymon Konicki
 *
 */

public class ParsedRequest {

	private final OpenRestQueryParameterHolder partTreeSpecification;
	private final Class<?> domainClass;
	private final PropertyPath propertyPath;

	public ParsedRequest(Class<?> domainClass,
			OpenRestQueryParameterHolder partTreeSpecification) {
		super();
		this.domainClass = domainClass;
		this.partTreeSpecification = partTreeSpecification;
		this.propertyPath = null;
	}

	public ParsedRequest(Class<?> domainClass,PropertyPath propertyPath,
			OpenRestQueryParameterHolder partTreeSpecification) {
		this.domainClass = domainClass;
		this.partTreeSpecification = partTreeSpecification;
		this.propertyPath = propertyPath;
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
}
