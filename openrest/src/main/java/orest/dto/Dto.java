package orest.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Dto {
	Class<?> entityType();
	String name() default "";
	Class<?> entityCreatorType() default void.class;
	Class<?> entityMergerType() default void.class;
	
}
