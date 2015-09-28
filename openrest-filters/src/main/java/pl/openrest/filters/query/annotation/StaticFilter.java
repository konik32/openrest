package pl.openrest.filters.query.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark predicate method as static filter. Returned predicate
 * will be added to every query for entity.
 * 
 * @author Szymon Konicki
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StaticFilter {

	/**
	 * SpEL String that has to evaluate to boolean. If true static filter won't
	 * be added to query.
	 * 
	 */
	String offOnCondition() default "";

	/**
	 * Array of SpEl Strings that after evaluation will be passed to expression
	 * method as its parameters
	 */
	String[] parameters() default {};
}
