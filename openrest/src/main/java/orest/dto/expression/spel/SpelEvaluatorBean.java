package orest.dto.expression.spel;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import orest.dto.Dto;
import orest.dto.handler.DtoHandler;
import orest.util.traverser.ObjectGraphTraverser;
import orest.util.traverser.TraverserCallback;
import orest.util.traverser.TraverserFieldFilter;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * This bean evaluates {@link Dto}'s field annotated with {@link Value}. Nested
 * {@link Dto}s and parametarized {@link Iterable} of {@link Dto} are also
 * evaluated.
 * 
 * @author Szymon Konicki
 *
 */
public class SpelEvaluatorBean implements DtoHandler {

	private final BeanFactory beanFactory;

	public SpelEvaluatorBean(BeanFactory beanFactory) {
		Assert.notNull(beanFactory);
		this.beanFactory = beanFactory;
	}

	private void evaluate(final DtoEvaluationWrapper wrapper) {
		ObjectGraphTraverser traverser = new ObjectGraphTraverser(new TraverserCallback() {
			@Override
			public void doWith(Field field, Object owner, String path) throws IllegalArgumentException,
					IllegalAccessException {
				final SpelEvaluator spelEvaluator = new SpelEvaluator(new DtoEvaluationWrapper(owner, null),
						beanFactory);
				Object value = spelEvaluator.evaluate(field);
				field.set(owner, value);
			}
		}, new TraverserFieldFilter() {

			@Override
			public boolean matches(Field field, Object owner, String path) {
				if (Iterable.class.isAssignableFrom(field.getType())) {
					Type type = field.getGenericType();
					if (type instanceof ParameterizedType) {
						ParameterizedType pType = (ParameterizedType) type;
						Type[] arr = pType.getActualTypeArguments();
						return AnnotationUtils.isAnnotationDeclaredLocally(Dto.class, (Class<?>) arr[0]);
					} else {
						// Collections must be parameterized;
						return false;
					}
				} else {
					return AnnotationUtils.isAnnotationDeclaredLocally(Dto.class, field.getType());
				}
			}
		}, new TraverserFieldFilter() {

			@Override
			public boolean matches(Field field, Object owner, String path) {
				return field.isAnnotationPresent(Value.class);
			}
		});

		traverser.traverse(wrapper.getDto());
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
