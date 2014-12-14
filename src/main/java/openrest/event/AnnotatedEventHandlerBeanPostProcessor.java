package openrest.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import openrest.dto.DtoPopulatorEvent;
import openrest.dto.DtoPopulatorHandler;
import openrest.event.AbstractAnnotatedMethodHandlerBeanPostProcessor.AnnotationEventWrapper;
import openrest.event.AbstractAnnotatedMethodHandlerBeanPostProcessor.HandlerMethod;
import openrest.event.annotation.HandleAfterCollectionGet;
import openrest.event.annotation.HandleAfterGet;
import openrest.event.annotation.HandleBeforeCollectionGet;
import openrest.event.annotation.HandleBeforeGet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.event.AnnotatedHandlerBeanPostProcessor;
import org.springframework.data.rest.core.event.RepositoryEvent;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

public class AnnotatedEventHandlerBeanPostProcessor extends AbstractAnnotatedMethodHandlerBeanPostProcessor implements
		ApplicationListener<ResourceSupportEvent> {
	private static final Logger LOG = LoggerFactory.getLogger(AnnotatedEventHandlerBeanPostProcessor.class);
	protected final MultiValueMap<Object, HandlerMethod> handlerMethods = new LinkedMultiValueMap<Object, AbstractAnnotatedMethodHandlerBeanPostProcessor.HandlerMethod>();

	@Override
	public void onApplicationEvent(ResourceSupportEvent event) {
		Class<? extends ResourceSupportEvent> eventType = event.getClass();
		if (!handlerMethods.containsKey(eventType)) {
			return;
		}

		for (HandlerMethod handlerMethod : handlerMethods.get(eventType)) {
			try {
				Object src = event.getSource();

				if (!ClassUtils.isAssignable(handlerMethod.targetType, ((ResourceSupportEvent) event).getResourceType())) {
					continue;
				}

				List<Object> params = new ArrayList<Object>();
				params.add(src);
				if (LOG.isDebugEnabled()) {
					LOG.debug("Invoking " + event.getClass().getSimpleName() + " handler for " + event.getSource());
				}
				handlerMethod.method.invoke(handlerMethod.handler, params.toArray());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

	@Override
	protected void addHandlerMethod(Class<?> type, Object handler, Method method, Annotation anno, Class<?> eventType) {
		handlerMethods.add(eventType, new HandlerMethod(type, handler, method));
	}

	@Override
	protected List<AnnotationEventWrapper> getAnnotationEventWrappers() {
		return new ArrayList<AnnotationEventWrapper>(Arrays.asList(
				//
				new AnnotationEventWrapper(HandleBeforeCollectionGet.class, BeforeCollectionGetEvent.class),//
				new AnnotationEventWrapper(HandleBeforeGet.class, BeforeGetEvent.class), new AnnotationEventWrapper(HandleAfterGet.class, AfterGetEvent.class),
				new AnnotationEventWrapper(HandleAfterCollectionGet.class, AfterCollectionGetEvent.class)//
				));
	}

	// @Override
	// public Object postProcessBeforeInitialization(Object bean, String
	// beanName) throws BeansException {
	// return bean;
	// }
	//
	// @Override
	// public Object postProcessAfterInitialization(final Object bean, String
	// beanName) throws BeansException {
	// final Class<?> beanType = bean.getClass();
	//
	// RepositoryEventHandler typeAnno =
	// AnnotationUtils.findAnnotation(beanType, RepositoryEventHandler.class);
	// if (null == typeAnno) {
	// return bean;
	// }
	//
	// Class<?>[] targetTypes = typeAnno.value();
	// if (targetTypes.length == 0) {
	// targetTypes = new Class<?>[] { null };
	// }
	//
	// for (final Class<?> targetType : targetTypes) {
	// ReflectionUtils.doWithMethods(beanType, new
	// ReflectionUtils.MethodCallback() {
	// @Override
	// public void doWith(Method method) throws IllegalArgumentException,
	// IllegalAccessException {
	// inspect(targetType, bean, method, HandleBeforeGet.class,
	// BeforeGetEvent.class);
	// inspect(targetType, bean, method, HandleAfterGet.class,
	// AfterGetEvent.class);
	// inspect(targetType, bean, method, HandleBeforeCollectionGet.class,
	// BeforeCollectionGetEvent.class);
	// inspect(targetType, bean, method, HandleAfterCollectionGet.class,
	// AfterCollectionGetEvent.class);
	// }
	// }, new ReflectionUtils.MethodFilter() {
	// @Override
	// public boolean matches(Method method) {
	// return (!method.isSynthetic() && !method.isBridge() &&
	// method.getDeclaringClass() != Object.class &&
	// !method.getName().contains("$"));
	// }
	// });
	// }
	//
	// return bean;
	// }
	//
	// private <T extends Annotation> void inspect(Class<?> targetType, Object
	// handler, Method method, Class<T> annoType,
	// Class<? extends ResourceSupportEvent> eventType) {
	// T anno = method.getAnnotation(annoType);
	// if (null != anno) {
	// try {
	// Class<?>[] targetTypes;
	// if (null == targetType) {
	// targetTypes = (Class<?>[]) anno.getClass().getMethod("value", new
	// Class[0]).invoke(anno);
	// } else {
	// targetTypes = new Class<?>[] { targetType };
	// }
	// for (Class<?> type : targetTypes) {
	// EventHandlerMethod m = new EventHandlerMethod(type, handler, method);
	// if (LOG.isDebugEnabled()) {
	// LOG.debug("Annotated handler method found: " + m);
	// }
	// handlerMethods.add(eventType, m);
	// }
	// } catch (NoSuchMethodException e) {
	// if (LOG.isDebugEnabled()) {
	// LOG.debug(e.getMessage(), e);
	// }
	// } catch (InvocationTargetException e) {
	// if (LOG.isDebugEnabled()) {
	// LOG.debug(e.getMessage(), e);
	// }
	// } catch (IllegalAccessException e) {
	// if (LOG.isDebugEnabled()) {
	// LOG.debug(e.getMessage(), e);
	// }
	// }
	// }
	// }
	//
	// private class EventHandlerMethod {
	// final Class<?> targetType;
	// final Method method;
	// final Object handler;
	//
	// private EventHandlerMethod(Class<?> targetType, Object handler, Method
	// method) {
	// this.targetType = targetType;
	// this.method = method;
	// this.handler = handler;
	// }
	//
	// @Override
	// public String toString() {
	// return "HandlerMethod{" + "targetType=" + targetType + ", method=" +
	// method + ", handler=" + handler + '}';
	// }
	// }

}
