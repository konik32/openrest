package pl.openrest.filters.query.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Helper annotation to be used with {@link ExpressionMethod} specify joins that
 * will be created when {@link ExpressionMethod} is used in query. Association
 * specified in value will be left joined with entity.
 * 
 * @author Szymon Konicki
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Join {
	/**
	 * Flag indicating whether associations should be also fetched
	 */
	boolean fetch() default false;

	/**
	 * 
	 * Association name
	 */
	String value();
}
