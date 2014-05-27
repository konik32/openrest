package pl.stalkon.data.boost.response.filter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

public class RequestBasedFilter extends SimpleBeanPropertyFilter {

	
	private final ResponseFilterInvoker filterInvoker;
	private final HttpServletRequest request;
	

	public RequestBasedFilter(ResponseFilterInvoker filterInvoker,
			HttpServletRequest request) {
		super();
		this.filterInvoker = filterInvoker;
		this.request = request;
	}


	@Override
	protected boolean include(BeanPropertyWriter writer) {
		return filterInvoker.include(writer, request);
	}

}
