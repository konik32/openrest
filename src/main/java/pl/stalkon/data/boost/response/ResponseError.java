package pl.stalkon.data.boost.response;

public class ResponseError {

	private final int status;
	private final String msg;
	private final int code;

	public ResponseError(int status, int code, String msg) {
		this.status = status;
		this.msg = msg;
		this.code = code;
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
