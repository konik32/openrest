package openrest.event;

import openrest.webmvc.ParsedRequest;

import org.springframework.data.rest.core.event.RepositoryEvent;

public class BeforeCollectionGetEvent extends ResourceSupportEvent {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5285665399560583629L;

	public BeforeCollectionGetEvent(ParsedRequest source, Class<?> resourceType) {
		super(source, resourceType);
	}

}
