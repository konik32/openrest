package orest.dto.authorization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import orest.dto.authorization.AuthorizationStrategy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AuthStrategies {
	Class<? extends AuthorizationStrategy>[] value();
}
