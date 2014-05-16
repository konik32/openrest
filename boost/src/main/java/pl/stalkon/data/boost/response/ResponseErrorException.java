package pl.stalkon.data.boost.response;

public abstract class ResponseErrorException extends Exception {

	private static final long serialVersionUID = -4359708766628262346L;

	private final int status;
	private final String msg;
	private final int code;

	public ResponseErrorException(int status, String msg, int code) {
		this.status = status;
		this.msg = msg;
		this.code = code;
	}

	public ResponseError getResponseError(){
		return new ResponseError(status, code, msg);
	}
	
	public int getStatus() {
		return status;
	}

	public String getMsg() {
		return msg;
	}

	public int getCode() {
		return code;
	}
}
