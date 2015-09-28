package pl.openrest.dto.event;


public class AfterSaveWithDtoEvent extends RepositoryWithDtoEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1398264999298575110L;

	/**
	 * 
	 */

	public AfterSaveWithDtoEvent(Object source, Object dto) {
		super(source, dto);
	}

}
