package pl.openrest.dto.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.validation.DtoFieldExpressionValidator;

/**
 * {@link DtoFieldExpressionValidator} validates {@link Dto} fields marked with this annotation based on specified expression in value
 * parameter. If dto contains any object that should be validated too, it should be marked with {@link Valid}. The expression context holds
 * field's owner object with name 'dto' and respective entity object with name 'entity'.
 * 
 * @author Szymon Konicki
 *
 */

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateExpression {

    String value() default "";

    String message() default "";
}
