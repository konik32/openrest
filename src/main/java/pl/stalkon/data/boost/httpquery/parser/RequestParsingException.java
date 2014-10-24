package pl.stalkon.data.boost.httpquery.parser;

public class RequestParsingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1479091902430062202L;

	public RequestParsingException(String message){
		this(message, null);
	}
	
	public RequestParsingException(String message,Throwable cause){
		super(message, cause);
	}
}
