package pl.stalkon.data.boost.response.filter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FilteringObjectMapper extends ObjectMapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1319303561608382812L;

	public FilteringObjectMapper(ObjectMapper src){
		super(src);
		this.setAnnotationIntrospector(new RequestBasedFilterIntrospector());
	}
}
