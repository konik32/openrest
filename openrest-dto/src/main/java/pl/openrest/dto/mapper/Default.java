package pl.openrest.dto.mapper;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Target(value=ElementType.TYPE)
@Retention(RUNTIME)
public @interface Default {

}
