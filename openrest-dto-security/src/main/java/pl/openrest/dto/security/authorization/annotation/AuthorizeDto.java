package pl.openrest.dto.security.authorization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pl.openrest.dto.security.authorization.DtoAuthorizationStrategy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AuthorizeDto {
    Class<? extends DtoAuthorizationStrategy>[] value();
}
