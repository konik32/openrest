package pl.stalkon.data.boost.response.filter;

import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class RequestBasedFilterIntrospector extends JacksonAnnotationIntrospector {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3275248572847745240L;

	@Override
	public Object findFilterId(AnnotatedClass ac) {
		return "requestBasedFilter";
	}
}
