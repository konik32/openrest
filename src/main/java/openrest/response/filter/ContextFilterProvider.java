package openrest.response.filter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class ContextFilterProvider extends SimpleFilterProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5022946990133224100L;
	
	
	private Map<String, Class<? extends AbstractContextFilter>> contextFilters =  new HashMap<String, Class<? extends AbstractContextFilter>>();
	
	@Autowired
	private ContextFilterFactory spelMultiplePropertyFilterFactory ;
	
	@Override
	public PropertyFilter findPropertyFilter(Object filterId,
			Object valueToFilter) {
		if(contextFilters.containsKey(filterId)){
			return spelMultiplePropertyFilterFactory.get(valueToFilter, contextFilters.get(filterId));
		}else{
			return super.findPropertyFilter(filterId, valueToFilter);
		}
		
	}
	
	public void addContextFilter(String filterId,  Class<? extends AbstractContextFilter> contextFilterType){
		contextFilters.put(filterId, contextFilterType);
	}
	
}
