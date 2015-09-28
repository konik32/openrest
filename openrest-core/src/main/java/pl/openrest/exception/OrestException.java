package pl.openrest.exception;

import lombok.Getter;
import lombok.ToString;
@ToString
public class OrestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6927683705510761229L;

	private @Getter final int code;

	public OrestException(int code, String message) {
		super(message);
		this.code = code;
	}

}
