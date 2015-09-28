package pl.openrest.dto.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If a dto is used with PATCH requests, there is no way to tell if its field
 * was set to {@literal null} or wasn't initialized. Such field should be mark
 * with this annotation.
 * 
 * @author Szymon Konicki
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface Nullable {

	/**
	 * Name of a boolean flag field which holds information whether this field
	 * was set to null or not initialized eg.
	 * 
	 * <pre>
	 * 
	 * @Nullable("nameSet")
	 * private String name;
	 * private boolean nameSet = false;
	 * 
	 * public void setName(String name){
	 * 	this.name = name;
	 * 	this.nameSet = true; 
	 * }
	 * </pre>
	 */
	String value();
}
