package openrest.event;

import openrest.webmvc.ParsedRequest;

import org.springframework.data.rest.core.event.RepositoryEvent;

public class BeforeGetEvent extends ResourceSupportEvent {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6684832500473237316L;

	public BeforeGetEvent(ParsedRequest source, Class<?> resourceType) {
		super(source,resourceType);
	}


}
