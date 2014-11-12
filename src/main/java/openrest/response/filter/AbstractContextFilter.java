package openrest.response.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

public abstract class AbstractContextFilter extends SimpleBeanPropertyFilter {

	protected Set<String> propertiesToIgnore;

	public abstract void prepare(HttpServletRequest request,
			Object valueToFilter);
	
	protected void setPropertiesToIgnoreFromContextFilterAnnotation(Object valueToFilter) {
		ContextFilter cf = AnnotationUtils.findAnnotation(
				valueToFilter.getClass(), ContextFilter.class);
		Assert.notNull(cf, "Method handles only object with ContextFilter annotation");
		addAll(propertiesToIgnore);
	}
	
	protected void addAll(Collection<String> propertiesToIgnore){
		propertiesToIgnore = new HashSet<String>(propertiesToIgnore.size());
		propertiesToIgnore.addAll(propertiesToIgnore);
	}

	@Override
	protected boolean include(BeanPropertyWriter writer) {
		return propertiesToIgnore == null ? true : propertiesToIgnore
				.contains(writer.getName());
	}

	@Override
	protected boolean include(PropertyWriter writer) {
		return propertiesToIgnore == null ? true : !propertiesToIgnore
				.contains(writer.getName());
	}

}
