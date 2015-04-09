package orest.exception;

import lombok.Data;

@Data
public class OrestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6927683705510761229L;

	private final int code;

	public OrestException(int code, String message) {
		super(message);
		this.code = code;
	}

}
