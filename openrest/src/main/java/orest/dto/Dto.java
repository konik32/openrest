package orest.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark class as DTO for specified entity.
 * 
 * @author Szymon Konicki
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Dto {

	/**
	 * Entity type which will be created from or merged with this dto
	 */
	Class<?> entityType();

	/**
	 * Name of this dto which should be passed in POST, PUT, PATCH query
	 * parameter named {@literal dto}. When name is not specified dto won't be exported, and could be used only as nested object in other dto.
	 * 
	 */
	String name() default "";

	/**
	 * Type of custom {@link EntityFromDtoCreator} that will be used to create
	 * entity from dto.
	 * 
	 */
	Class<? extends EntityFromDtoCreator> entityCreatorType() default DefaultEntityFromDtoCreator.class;

	/**
	 * Type of custom {@link EntityFromDtoMerger} that will be used to merge
	 * entity with dto.
	 * 
	 */
	Class<? extends EntityFromDtoMerger> entityMergerType() default DefaultEntityFromDtoCreator.class;

}
