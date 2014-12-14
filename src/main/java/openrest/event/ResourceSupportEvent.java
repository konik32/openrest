package openrest.event;

import org.springframework.context.ApplicationEvent;

public class ResourceSupportEvent extends ApplicationEvent {

	private static final long serialVersionUID = 3610965768424915340L;
	protected final Class<?> resourceType;

	public ResourceSupportEvent(Object source, Class<?> resourceType) {
		super(source);
		this.resourceType = resourceType;
	}

	public Class<?> getResourceType() {
		return resourceType;
	}

}
