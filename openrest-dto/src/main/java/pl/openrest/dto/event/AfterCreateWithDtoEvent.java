package pl.openrest.dto.event;


public class AfterCreateWithDtoEvent extends RepositoryWithDtoEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7444025745216725097L;

	public AfterCreateWithDtoEvent(Object source, Object dto) {
		super(source, dto);
	}

}
