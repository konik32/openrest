package orest.dto.expression.spel;

import java.lang.reflect.Field;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class SpelEvaluatorBean {

	private final BeanFactory beanFactory;

	public SpelEvaluatorBean(BeanFactory beanFactory) {
		Assert.notNull(beanFactory);
		this.beanFactory = beanFactory;
	}

	public void evaluate(final DtoEvaluationWrapper wrapper) {
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
}
