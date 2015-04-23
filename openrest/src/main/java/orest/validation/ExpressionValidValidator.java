package orest.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.Data;
import orest.expression.SpelEvaluator;
import orest.validation.UpdateValidationContext;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ExpressionValidValidator implements ConstraintValidator<ExpressionValid, Object> {

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private UpdateValidationContext validationContext;

	private String expression;

	@Override
	public void initialize(final ExpressionValid constraintAnnotation) {
		this.expression = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		final SpelEvaluator spelEvaluator = new SpelEvaluator(new ContextWrapper(validationContext.getDto(),
				validationContext.getEntity()), beanFactory, false);
		return spelEvaluator.evaluateAsBoolean(expression);
	}

	@Data
	private class ContextWrapper {
		private final Object dto;
		private final Object entity;
	}
}