package openrest.security.validator;

public class NotAuthorizedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4234945099674658006L;

	public NotAuthorizedException(String message) {
		super(message);
	}

	public NotAuthorizedException() {
		super("Not authorized");
	}

}
