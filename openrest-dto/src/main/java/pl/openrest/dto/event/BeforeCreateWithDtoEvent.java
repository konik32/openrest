package pl.openrest.dto.event;


public class BeforeCreateWithDtoEvent extends RepositoryWithDtoEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7703809183177447708L;

	/**
	 * 
	 */

	public BeforeCreateWithDtoEvent(Object source, Object dto) {
		super(source, dto);
	}

}
