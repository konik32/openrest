package pl.stalkon.data.boost.response;

import com.fasterxml.jackson.annotation.JsonFilter;


public class Response {

	protected Object content;
	
	public Response(Object content) {
		this.content = content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public Object getContent() {
		return content;
	}

}
