package openrest.webmvc;

import openrest.domain.PartTreeSpecificationImpl;

import org.springframework.data.mapping.PropertyPath;
/**
 * Wrapper containing information about requested and filtered resource
 * @author Szymon Konicki
 *
 */

public class ParsedRequest {

	private final PartTreeSpecificationImpl partTreeSpecification;
	private final Class<?> domainClass;
	private final PropertyPath propertyPath;

	public ParsedRequest(Class<?> domainClass,
			PartTreeSpecificationImpl partTreeSpecification) {
		super();
		this.domainClass = domainClass;
		this.partTreeSpecification = partTreeSpecification;
		this.propertyPath = null;
	}

	public ParsedRequest(PropertyPath propertyPath,
			PartTreeSpecificationImpl partTreeSpecification) {
		this.domainClass = propertyPath.getType();
		this.partTreeSpecification = partTreeSpecification;
		this.propertyPath = propertyPath;
	}

	public PartTreeSpecificationImpl getPartTreeSpecification() {
		return partTreeSpecification;
	}

	public Class<?> getDomainClass() {
		return domainClass;
	}

	public PropertyPath getPropertyPath() {
		return propertyPath;
	}
}
