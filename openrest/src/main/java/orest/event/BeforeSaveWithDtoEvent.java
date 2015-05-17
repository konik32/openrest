package orest.event;


public class BeforeSaveWithDtoEvent extends RepositoryWithDtoEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4908878116006674371L;

	public BeforeSaveWithDtoEvent(Object source, Object dto) {
		super(source, dto);
	}

}
