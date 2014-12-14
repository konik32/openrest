package openrest.dto;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import openrest.event.AbstractAnnotatedMethodHandlerBeanPostProcessor;

public class DtoPopulatorInvoker extends AbstractAnnotatedMethodHandlerBeanPostProcessor implements ApplicationListener<DtoPopulatorEvent> {
	private static final Logger LOG = LoggerFactory.getLogger(DtoPopulatorInvoker.class);
	protected final MultiValueMap<String, HandlerMethod> handlerMethods = new LinkedMultiValueMap<String, AbstractAnnotatedMethodHandlerBeanPostProcessor.HandlerMethod>();

	@Override
	public void onApplicationEvent(DtoPopulatorEvent event) {
		for (String dto : event.getDtos()) {
			if (!handlerMethods.containsKey(dto)) {
				continue;
			}

			for (HandlerMethod handlerMethod : handlerMethods.get(dto)) {
				try {
					Object src = event.getSource();
					if (!ClassUtils.isAssignable(handlerMethod.getTargetType(), src.getClass())) {
						continue;
					}

					List<Object> params = new ArrayList<Object>();
					params.add(src);
					params.add(dto);
					if (LOG.isDebugEnabled()) {
						LOG.debug("Invoking " + src.getClass().getSimpleName() + " handler for " + src);
					}
					List<EmbeddedWrapper> dtoEmbeddeds = (List<EmbeddedWrapper>) handlerMethod.getMethod().invoke(handlerMethod.getHandler(), params.toArray());
					if (dtoEmbeddeds != null)
						event.getEmbeddeds().addAll(dtoEmbeddeds);
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		}

	}


	@Override
	protected void addHandlerMethod(Class<?> type, Object handler, Method method, Annotation anno, Class<?> eventType) {
		String[] dtos = ((DtoPopulatorHandler) anno).dtos();
		for (String dto : dtos) {
			handlerMethods.add(dto, new DtoAwareHandlerMethod(type, handler, method, dto));
		}
	}

	@Override
	protected List<AnnotationEventWrapper> getAnnotationEventWrappers() {
		return new ArrayList<AnnotationEventWrapper>(Arrays.asList(new AnnotationEventWrapper(DtoPopulatorHandler.class, DtoPopulatorEvent.class)));
	}

	private class DtoAwareHandlerMethod extends HandlerMethod {
		final String dto;

		public DtoAwareHandlerMethod(Class<?> targetType, Object handler, Method method, String dto) {
			super(targetType, handler, method);
			this.dto = dto;
		}

	}

}
