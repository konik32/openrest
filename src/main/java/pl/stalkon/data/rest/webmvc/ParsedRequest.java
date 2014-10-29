package pl.stalkon.data.rest.webmvc;

import org.springframework.data.mapping.PropertyPath;

import pl.stalkon.data.boost.domain.PartTreeSpecification;
/**
 * Wrapper containing information about requested and filtered resource
 * @author Szymon Konicki
 *
 */

public class ParsedRequest {

	private final PartTreeSpecification partTreeSpecification;
	private final Class<?> domainClass;
	private final PropertyPath propertyPath;

	public ParsedRequest(Class<?> domainClass,
			PartTreeSpecification partTreeSpecification) {
		super();
		this.domainClass = domainClass;
		this.partTreeSpecification = partTreeSpecification;
		this.propertyPath = null;
	}

	public ParsedRequest(PropertyPath propertyPath,
			PartTreeSpecification partTreeSpecification) {
		this.domainClass = propertyPath.getType();
		this.partTreeSpecification = partTreeSpecification;
		this.propertyPath = propertyPath;
	}

	public PartTreeSpecification getPartTreeSpecification() {
		return partTreeSpecification;
	}

	public Class<?> getDomainClass() {
		return domainClass;
	}

	public PropertyPath getPropertyPath() {
		return propertyPath;
	}
}
