package orest.dto.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import orest.dto.validation.ExpressionValidValidator;

@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExpressionValidValidator.class)
@Documented
public @interface ExpressionValid {
	String value();

	String message() default "{constraints.expressionValid}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
