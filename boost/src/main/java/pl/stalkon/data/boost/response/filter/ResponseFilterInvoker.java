package pl.stalkon.data.boost.response.filter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public class ResponseFilterInvoker {

	private List<ResponseFilter> filters = new ArrayList<ResponseFilter>();
	private FiltersContext filtersContext = new FiltersContext();

	public void addFilter(ResponseFilter filter) {
		filters.add(filter);
	}

	public boolean include(BeanPropertyWriter writer, HttpServletRequest request) {
		for (ResponseFilter filter : filters) {
			if (!filter.include(writer, request, filtersContext))
				return false;
		}
		return true;
	}

}
