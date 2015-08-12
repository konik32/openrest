package orest.dto.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.validation.Valid;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import orest.dto.Dto;
import orest.dto.expression.spel.DtoEvaluationWrapper;
import orest.dto.expression.spel.SpelEvaluator;
import orest.dto.validation.annotation.ValidateExpression;
import orest.util.traverser.AnnotationFieldFilter;
import orest.util.traverser.ObjectGraphTraverser;
import orest.util.traverser.TraverserCallback;

import org.hibernate.mapping.Map;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class DtoFieldExpressionValidator implements Validator {

	private final String FIELD_NAME_MESSAGE_FORMAT = "Field: %s validation expression evaluated to false";

	private UpdateValidationContext updateValidationContext;

	private BeanFactory beanFactory;

	private @Setter Class<? extends Annotation> traversingAnnotation = Valid.class;

	@Autowired
	public DtoFieldExpressionValidator(@NonNull UpdateValidationContext updateValidationContext,
			@NonNull BeanFactory beanFactory) {
		this.updateValidationContext = updateValidationContext;
		this.beanFactory = beanFactory;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return AnnotationUtils.isAnnotationDeclaredLocally(Dto.class, clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		final TraversingStack traversingStack = new TraversingStack(updateValidationContext.getEntity());

		ObjectGraphTraverser traverser = new ObjectGraphTraverser(new ValidationCallback(traversingStack, errors),
				new AnnotationFieldFilter(traversingAnnotation), new AnnotationFieldFilter(ValidateExpression.class));

		traverser.setBeforeTraverse(new BeforeTraverseCallback(traversingStack));

		traverser.setAfterTraverse(new AfterTraverseCallback(traversingStack) {

		});

		traverser.traverse(target);
	}

	private class ValidationCallback implements TraverserCallback {
		private TraversingStack traversingStack;
		private Errors errors;

		public ValidationCallback(TraversingStack traversingStack, Errors errors) {
			this.traversingStack = traversingStack;
			this.errors = errors;
		}

		@Override
		public void doWith(Field field, Object owner, String path) throws IllegalArgumentException,
				IllegalAccessException {
			ValidateExpression ann = AnnotationUtils.getAnnotation(field, ValidateExpression.class);
			DtoEvaluationWrapper wrapper = new DtoEvaluationWrapper(owner, traversingStack.getEntity());
			SpelEvaluator spelEvaluator = new SpelEvaluator(wrapper, beanFactory);
			boolean isValid = spelEvaluator.evaluateAsBoolean(ann.value());
			if (!isValid) {
				String message = ann.message().isEmpty() ? String.format(FIELD_NAME_MESSAGE_FORMAT, field.getName())
						: ann.message();
				errors.rejectValue(path, ValidateExpression.class.getSimpleName(), message);
			}
		}
	}

	private class BeforeTraverseCallback implements TraverserCallback {

		private TraversingStack traversingStack;

		public BeforeTraverseCallback(TraversingStack traversingContext) {
			this.traversingStack = traversingContext;
		}

		@Override
		public void doWith(Field field, Object owner, String path) throws IllegalArgumentException,
				IllegalAccessException {
			if (traversingStack.getEntity() == null || isCollectionLike(field.getType())) {
				traversingStack.push(null);
			} else {
				Field entityField = ReflectionUtils.findField(traversingStack.getEntity().getClass(), field.getName());
				if (entityField == null) {
					traversingStack.push(null);
				} else {
					ReflectionUtils.makeAccessible(entityField);
					Object entityValue = ReflectionUtils.getField(entityField, traversingStack.getEntity());
					traversingStack.push(entityValue);
				}
			}
		}

		private boolean isCollectionLike(Class<?> type) {
			return Iterable.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
		}
	}

	private class AfterTraverseCallback implements TraverserCallback {
		private TraversingStack traversingContext;

		public AfterTraverseCallback(TraversingStack traversingContext) {
			this.traversingContext = traversingContext;
		}

		@Override
		public void doWith(Field field, Object owner, String path) throws IllegalArgumentException,
				IllegalAccessException {
			traversingContext.pop();
		}
	}

	private class TraversingStack {
		private TraversingNode current;

		public TraversingStack(Object entity) {
			push(entity);
		}

		public void pop() {
			current = current.parent;
		}

		public void push(Object entity) {
			current = new TraversingNode(current, entity);
		}

		public void push() {
			current = new TraversingNode(current, null);
		}

		public Object getEntity() {
			return current.entity;
		}

		@RequiredArgsConstructor
		private class TraversingNode {
			private final TraversingNode parent;
			private final Object entity;

		}
	}

	// public void validateRecursively(Object dto, Object entity, Errors errors)
	// {
	// if (dto == null)
	// return;
	// final SpelEvaluator spelEvaluator = new SpelEvaluator(new
	// DtoEvaluationWrapper(dto, entity), beanFactory);
	// ReflectionUtils.doWithFields(dto.getClass(), new FieldCallback() {
	//
	// @Override
	// public void doWith(Field field) throws IllegalArgumentException,
	// IllegalAccessException {
	// if (field.isAnnotationPresent(ValidateExpression.class))
	// doWithValidationExpression(field, spelEvaluator, errors);
	// if (field.isAnnotationPresent(Valid.class))
	// doWithValid(dto, entity, errors, field);
	// }
	//
	// }, new ExpressionValidationMarkedFieldsFilter());
	// }
	//
	// private class ExpressionValidationMarkedFieldsFilter implements
	// FieldFilter {
	//
	// @Override
	// public boolean matches(Field field) {
	// return field.isAnnotationPresent(ValidateExpression.class) ||
	// field.isAnnotationPresent(Valid.class);
	// }
	//
	// }
	//
	// private void doWithValid(Object dto, Object entity, Errors errors, Field
	// field) {
	// Field entityField = null;
	// if (entity != null)
	// entityField = ReflectionUtils.findField(entity.getClass(),
	// field.getName());
	// Object entityFieldValue = null;
	// if (entityField != null) {
	// ReflectionUtils.makeAccessible(entityField);
	// entityFieldValue = ReflectionUtils.getField(entityField, entity);
	// }
	// ReflectionUtils.makeAccessible(field);
	// Object dtoFieldValue = ReflectionUtils.getField(field, dto);
	// validateRecursively(dtoFieldValue, entityFieldValue, errors);
	// }
	//
}
