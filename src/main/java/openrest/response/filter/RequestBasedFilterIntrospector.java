package openrest.response.filter;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class RequestBasedFilterIntrospector extends JacksonAnnotationIntrospector {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3275248572847745240L;

	@Override
	public Object findFilterId(Annotated a) {
		if(a.hasAnnotation(SpelFilter.class))
			return SpelMultiplePropertyFilter.FILTER_ID;
		if(a.hasAnnotation(ContextFilter.class))
			return ((ContextFilter) a).filterId();
		return super.findFilterId(a);
	}
}
