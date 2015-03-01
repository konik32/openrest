package orest.expression.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExpressionMethod {

	boolean exported() default true;
	String name() default "";
	boolean defaultedPageable() default true;
	boolean searchMethod() default false;
	Join[] joins() default {};
}
