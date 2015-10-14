package pl.openrest.filters.predicate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pl.openrest.filters.query.annotation.Join;

/**
 * 
 * @author Szymon Konicki
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Predicate {

    String name() default "";

    boolean defaultedPageable() default true;

    /**
     * Flag indicating whether this expression method could be used as search method in url /{repository}/search/{searchMethod}
     */

    PredicateType type() default PredicateType.FILTER;

    /**
     * Joins that will be added to query
     * 
     */

    Join[] joins() default {};

    public enum PredicateType {
        SEARCH, FILTER, STATIC_FILTER, SORT;
    }
}
