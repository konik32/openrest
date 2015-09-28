package pl.openrest.filters.predicate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Annotation to mark class that declares {@link Predicate}s for entity
 * specified in value.
 * 
 * @author Szymon Konicki
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Component
public @interface PredicateRepository {

	Class<?> value();
	boolean defaultedPageable() default true;
}
