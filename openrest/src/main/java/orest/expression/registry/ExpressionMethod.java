package orest.expression.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**

 * @author Szymon Konicki
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExpressionMethod {
	/**
	 * Flag indicating whether this expression method could be used in filters
	 * query parameter
	 * 
	 */
	boolean exported() default true;

	String name() default "";

	boolean defaultedPageable() default true;

	/**
	 * Flag indicating whether this expression method could be used as search method in url /{repository}/search/{searchMethod}
	 */
	
	boolean searchMethod() default false;
	
	
	/**
	 * Joins that will be added to query
	 * 
	 */

	Join[] joins() default {};
}
