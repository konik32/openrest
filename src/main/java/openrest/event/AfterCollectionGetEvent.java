package openrest.event;

import org.springframework.data.rest.core.event.RepositoryEvent;
import org.springframework.hateoas.Resources;

public class AfterCollectionGetEvent extends ResourceSupportEvent {



	/**
	 * 
	 */
	private static final long serialVersionUID = 2616044157225607293L;

	public AfterCollectionGetEvent(Object source, Class<?> resourceType) {
		super(source, resourceType);
	}

}
