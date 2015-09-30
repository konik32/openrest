package pl.openrest.dto.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import lombok.Setter;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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
                if (pType.getRawType().equals(CreateMapper.class)) {
                    createMappers.put((Class<?>) pType.getActualTypeArguments()[1], (CreateMapper) bean);
                } else if (pType.getRawType().equals(UpdateMapper.class)) {
                    updateMappers.put((Class<?>) pType.getActualTypeArguments()[1], (UpdateMapper) bean);
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        for (Object bean : applicationContext.getBeansOfType(CreateMapper.class).values()) {
            registerMappers(bean);
        }
        for (Object bean : applicationContext.getBeansOfType(UpdateMapper.class).values()) {
            registerMappers(bean);
        }
    }

}
