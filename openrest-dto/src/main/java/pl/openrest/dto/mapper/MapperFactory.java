package pl.openrest.dto.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import lombok.Setter;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

@SuppressWarnings("rawtypes")
public class MapperFactory implements ApplicationContextAware {

	private Map<Class<?>, CreateMapper> createMappers = new HashMap<>();
	private Map<Class<?>, UpdateMapper> updateMappers = new HashMap<>();

	private @Setter CreateMapper defaultCreateMapper;
	private @Setter UpdateMapper defaultUpdateMapper;

	public CreateMapper getCreateMapper(Class<?> dtoType) {
		CreateMapper mapperType = createMappers.get(dtoType);
		return mapperType == null ? defaultCreateMapper : mapperType;
	}

	public UpdateMapper getUpdateMapper(Class<?> dtoType) {
		UpdateMapper mapperType = updateMappers.get(dtoType);
		return mapperType == null ? defaultUpdateMapper : mapperType;
	}

	private void registerMappers(final Object bean) {
		Class<?> beanType = bean.getClass();
		for (Type i : beanType.getGenericInterfaces()) {
			if (i instanceof ParameterizedType) {
				ParameterizedType pType = (ParameterizedType) i;
				Class<?> dtoType = (Class<?>) pType.getActualTypeArguments()[1];
				if (pType.getRawType().equals(CreateMapper.class)) {
					if (!createMappers.containsKey(dtoType) || !isDefaultMapper(bean))
						createMappers.put(dtoType, (CreateMapper) bean);
				} else if (!updateMappers.containsKey(dtoType) || !isDefaultMapper(bean)) {
					updateMappers.put(dtoType, (UpdateMapper) bean);
				}
			}
		}
	}

	private boolean isDefaultMapper(Object bean) {
		return  AnnotationUtils.findAnnotation(bean.getClass(), Default.class) != null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		for (Object bean : applicationContext
				.getBeansOfType(CreateMapper.class).values()) {
			registerMappers(bean);
		}
		for (Object bean : applicationContext
				.getBeansOfType(UpdateMapper.class).values()) {
			registerMappers(bean);
		}
	}

}
