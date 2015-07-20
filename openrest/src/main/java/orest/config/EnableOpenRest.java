package orest.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import orest.repository.ExpressionJpaFactoryBean;

import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(ORestConfig.class)
@EnableJpaRepositories(repositoryFactoryBeanClass = ExpressionJpaFactoryBean.class)
public @interface EnableOpenRest {

}
