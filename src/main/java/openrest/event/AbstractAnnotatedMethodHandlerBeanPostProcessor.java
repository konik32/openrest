package openrest.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public abstract class AbstractAnnotatedMethodHandlerBeanPostProcessor implements BeanPostProcessor {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractAnnotatedMethodHandlerBeanPostProcessor.class);

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
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
					List<AnnotationEventWrapper> annEventWrappers = getAnnotationEventWrappers();
					Assert.notNull(annEventWrappers);
					for (AnnotationEventWrapper aew : annEventWrappers) {
						inspect(targetType, bean, method, aew.annotationClass, aew.eventClass);
					}
				}
			}, new ReflectionUtils.MethodFilter() {
				@Override
				public boolean matches(Method method) {
					return (!method.isSynthetic() && !method.isBridge() && method.getDeclaringClass() != Object.class && !method.getName().contains("$"));
				}
			});
		}

		return bean;
	}

	private <T extends Annotation> void inspect(Class<?> targetType, Object handler, Method method, Class<T> annoType, Class<?> eventType) {
		T anno = method.getAnnotation(annoType);
		if (null != anno) {
			try {
				Class<?>[] targetTypes;
				if (null == targetType) {
					targetTypes = (Class<?>[]) anno.getClass().getMethod("value", new Class[0]).invoke(anno);
				} else {
					targetTypes = new Class<?>[] { targetType };
				}
				for (Class<?> type : targetTypes) {
					addHandlerMethod(type, handler, method, anno, eventType);
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

	protected abstract void addHandlerMethod(Class<?> type, Object handler, Method method, Annotation anno, Class<?> eventType);

	protected abstract List<AnnotationEventWrapper> getAnnotationEventWrappers();

	protected class HandlerMethod {
		protected final Class<?> targetType;
		protected final Method method;
		protected final Object handler;

		protected HandlerMethod(Class<?> targetType, Object handler, Method method) {
			this.targetType = targetType;
			this.method = method;
			this.handler = handler;
		}

		@Override
		public String toString() {
			return "HandlerMethod{" + "targetType=" + targetType + ", method=" + method + ", handler=" + handler + '}';
		}

		public Class<?> getTargetType() {
			return targetType;
		}

		public Method getMethod() {
			return method;
		}

		public Object getHandler() {
			return handler;
		}
	}

	protected class AnnotationEventWrapper {
		final Class<? extends Annotation> annotationClass;
		final Class<?> eventClass;

		public AnnotationEventWrapper(Class<? extends Annotation> annotationClass, Class<?> eventClass) {
			super();
			this.annotationClass = annotationClass;
			this.eventClass = eventClass;
		}
	}

}
