package orest.dto.expression.spel;

import java.lang.reflect.Field;

import orest.dto.handler.DtoHandler;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class SpelEvaluatorBean implements DtoHandler {

	private final BeanFactory beanFactory;

	public SpelEvaluatorBean(BeanFactory beanFactory) {
		Assert.notNull(beanFactory);
		this.beanFactory = beanFactory;
	}

	private void evaluate(final DtoEvaluationWrapper wrapper) {
		final SpelEvaluator spelEvaluator = new SpelEvaluator(wrapper, beanFactory);
		ReflectionUtils.doWithFields(wrapper.getDto().getClass(), new FieldCallback() {

			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				Value annotation = field.getAnnotation(Value.class);
				if (annotation == null)
					return;
				Object value = spelEvaluator.evaluate(field);
				ReflectionUtils.makeAccessible(field);
				field.set(wrapper.getDto(), value);
			}
		});
	}

	@Override
	public void handle(Object dto) {
		handle(dto, null);
	}

	@Override
	public void handle(Object dto, Object entity) {
		DtoEvaluationWrapper wrapper = new DtoEvaluationWrapper(dto, entity);
		evaluate(wrapper);
	}
}
