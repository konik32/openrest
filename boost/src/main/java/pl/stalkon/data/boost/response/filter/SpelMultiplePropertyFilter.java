package pl.stalkon.data.boost.response.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public abstract class SpelMultiplePropertyFilter implements ResponseFilter {

	private static final String FILTER_VALUE_NAME = "filter";
	private static final String PROPERTIES = "properties";

	@SuppressWarnings("unchecked")
	@Override
	public boolean include(BeanPropertyWriter writer,
			HttpServletRequest request, FiltersContext context) {
		if (!context.contains(FILTER_VALUE_NAME,
				SpelMultiplePropertyFilter.class)) {
			String spelString = writer.getContextAnnotation(
					ExcludeProperties.class).filter();
			String properties[] = writer.getContextAnnotation(
					ExcludeProperties.class).properties();
			context.add(FILTER_VALUE_NAME,
					getSpelValue(writer, request, spelString),
					SpelMultiplePropertyFilter.class);
			context.add(PROPERTIES,
					new HashSet<String>(Arrays.asList(properties)),
					SpelMultiplePropertyFilter.class);
		}
		boolean include = (Boolean) context.get(FILTER_VALUE_NAME,
				SpelMultiplePropertyFilter.class);
		if (include)
			return true;
		Set<String> properties = (Set<String>) context.get(PROPERTIES,
				SpelMultiplePropertyFilter.class);
		
		return !properties.contains(writer.getName());
	}

	private boolean getSpelValue(BeanPropertyWriter writer,
			HttpServletRequest request, String spelString) {
		
	
		return false;
	}

}
