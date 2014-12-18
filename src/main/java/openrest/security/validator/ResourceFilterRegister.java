package openrest.security.validator;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class ResourceFilterRegister implements BeanPostProcessor {

	private final MultiValueMap<Class<?>, ResourceFilter<Object>> filters = new LinkedMultiValueMap<Class<?>, ResourceFilter<Object>>();

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (ResourceFilter.class.isAssignableFrom(bean.getClass())) {
			ResourceFilter<Object> filter = (ResourceFilter<Object>) bean;
			filters.add(filter.supports(), filter);
		}
		return bean;
	}

	public List<ResourceFilter<Object>> getResourceFilters(Class<?> type) {
		return filters.get(type);
	}

}
