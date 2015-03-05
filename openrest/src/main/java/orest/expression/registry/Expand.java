package orest.expression.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.rest.core.config.Projection;
/**
 * Associations specified in value will be fetched with entity specified in {@link Projection}. This annotation could only by added to classes annotated with {@link Projection}. 
 * 
 * @author Szymon Konicki
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Expand {

	/**
	 * Associations names delimited with coma
	 * 
	 */
	String value();
}
