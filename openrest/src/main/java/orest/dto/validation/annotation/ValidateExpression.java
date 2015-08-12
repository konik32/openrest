package orest.dto.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Valid;

import orest.dto.Dto;

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
