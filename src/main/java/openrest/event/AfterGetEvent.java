package openrest.event;

import org.springframework.data.rest.core.event.RepositoryEvent;

public class AfterGetEvent extends ResourceSupportEvent {

	public AfterGetEvent(Object source, Class<?> resourceType) {
		super(source, resourceType);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2169516750913649072L;


}
