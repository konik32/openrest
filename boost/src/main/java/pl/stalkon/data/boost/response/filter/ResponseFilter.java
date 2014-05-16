package pl.stalkon.data.boost.response.filter;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public interface ResponseFilter {
	boolean include(BeanPropertyWriter writer, HttpServletRequest request);
}
