package pl.openrest.dto.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import lombok.Setter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@SuppressWarnings("rawtypes")
public class MapperFactory implements BeanPostProcessor {

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

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
        registerMappers(bean);
        return bean;
    }

    private void registerMappers(final Object bean) {
        Class<?> beanType = bean.getClass();
        for (Type i : beanType.getGenericInterfaces()) {
            if (i instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) i;
                if (pType.getRawType().equals(CreateMapper.class)) {
                    createMappers.put((Class<?>) pType.getActualTypeArguments()[1], (CreateMapper) bean);
                } else if (pType.getRawType().equals(UpdateMapper.class)) {
                    updateMappers.put((Class<?>) pType.getActualTypeArguments()[1], (UpdateMapper) bean);
                }
            }
        }
    }

}
