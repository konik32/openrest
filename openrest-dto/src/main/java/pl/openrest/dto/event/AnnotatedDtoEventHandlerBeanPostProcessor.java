package pl.openrest.dto.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.event.RepositoryEvent;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import pl.openrest.dto.event.annotation.HandleAfterCreateWithDto;
import pl.openrest.dto.event.annotation.HandleAfterSaveWithDto;
import pl.openrest.dto.event.annotation.HandleBeforeCreateWithDto;
import pl.openrest.dto.event.annotation.HandleBeforeSaveWithDto;

/**
 * 
 * @author Szymon Konicki
 *
 */
public class AnnotatedDtoEventHandlerBeanPostProcessor implements ApplicationListener<RepositoryEvent>, BeanPostProcessor  {

	private static final Logger LOG = LoggerFactory.getLogger(AnnotatedDtoEventHandlerBeanPostProcessor.class);
	private final MultiValueMap<Class<? extends RepositoryEvent>, EventHandlerMethod> handlerMethods = new LinkedMultiValueMap<Class<? extends RepositoryEvent>, EventHandlerMethod>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationListener#onApplicationEvent(org
	 * .springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(RepositoryEvent event) {
		Class<? extends RepositoryEvent> eventType = event.getClass();
		if (!handlerMethods.containsKey(eventType)) {
			return;
		}

		for (EventHandlerMethod handlerMethod : handlerMethods.get(eventType)) {

			Object src = event.getSource();
			Object dto = ((RepositoryWithDtoEvent) event).getDto();

			if (!ClassUtils.isAssignable(handlerMethod.targetType, src.getClass())) {
				continue;
			}

			if (!ClassUtils.isAssignable(handlerMethod.dtoType, dto.getClass())) {
				continue;
			}

			List<Object> params = new ArrayList<Object>();
			params.add(src);
			params.add(dto);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Invoking {} handler for {}.", event.getClass().getSimpleName(), event.getSource());
			}

			ReflectionUtils.invokeMethod(handlerMethod.method, handlerMethod.handler, params.toArray());
		}
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
		final Class<?> beanType = bean.getClass();

		RepositoryEventHandler typeAnno = AnnotationUtils.findAnnotation(beanType, RepositoryEventHandler.class);

		if (null == typeAnno) {
			return bean;
		}

		Class<?>[] targetTypes = typeAnno.value();

		if (targetTypes.length == 0) {
			targetTypes = new Class<?>[] { null };
		}

		for (final Class<?> targetType : targetTypes) {
			ReflectionUtils.doWithMethods(beanType, new ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					inspect(targetType, bean, method, HandleBeforeCreateWithDto.class, BeforeCreateWithDtoEvent.class);
					inspect(targetType, bean, method, HandleAfterCreateWithDto.class, AfterCreateWithDtoEvent.class);
					inspect(targetType, bean, method, HandleBeforeSaveWithDto.class, BeforeSaveWithDtoEvent.class);
					inspect(targetType, bean, method, HandleAfterSaveWithDto.class, AfterSaveWithDtoEvent.class);
				}
			}, new ReflectionUtils.MethodFilter() {
				@Override
				public boolean matches(Method method) {
					return (!method.isSynthetic() && !method.isBridge() && method.getDeclaringClass() != Object.class && !method
							.getName().contains("$"));
				}
			});
		}

		return bean;
	}

	private <T extends Annotation> void inspect(Class<?> targetType, Object handler, Method method, Class<T> annoType,
			Class<? extends RepositoryEvent> eventType) {
		T anno = method.getAnnotation(annoType);
		if (null != anno) {
			try {
				Class<?>[] targetTypes;
				Class<?>[] dtoTypes = null;
				dtoTypes = (Class<?>[]) anno.getClass().getMethod("dto", new Class[0]).invoke(anno);
				if (null == targetType) {
					targetTypes = (Class<?>[]) anno.getClass().getMethod("value", new Class[0]).invoke(anno);
				} else {
					targetTypes = new Class<?>[] { targetType };
				}
				for (Class<?> type : targetTypes) {
					for (Class<?> dtoType : dtoTypes) {
						addHandlerMethod(type, dtoType, handler, method, annoType, eventType);
					}
				}
			} catch (NoSuchMethodException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage(), e);
				}
			} catch (InvocationTargetException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage(), e);
				}
			} catch (IllegalAccessException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage(), e);
				}
			}
		}
	}

	private <T extends Annotation> void addHandlerMethod(Class<?> tagetType, Class<?> dtoType, Object handler,
			Method method, Class<T> annoType, Class<? extends RepositoryEvent> eventType) {
		EventHandlerMethod m = new EventHandlerMethod(tagetType, dtoType, method, handler);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Annotated handler method found: {}", m);
		}
		handlerMethods.add(eventType, m);
	}

	@Data
	private class EventHandlerMethod {
		final Class<?> targetType;
		final Class<?> dtoType;
		final Method method;
		final Object handler;

		@Override
		public String toString() {
			return "EventHandlerMethod{" + "targetType=" + targetType + ", method=" + method + ", handler=" + handler
					+ '}';
		}
	}

}
