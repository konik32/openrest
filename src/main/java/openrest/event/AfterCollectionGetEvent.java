package openrest.event;


public class AfterCollectionGetEvent extends ResourceSupportEvent {



	/**
	 * 
	 */
	private static final long serialVersionUID = 2616044157225607293L;

	public AfterCollectionGetEvent(Object source, Class<?> resourceType) {
		super(source, resourceType);
	}

}
