package pl.openrest.filters.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import pl.openrest.filters.repository.PredicateContextAwareJpaFactoryBean;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableJpaRepositories(repositoryFactoryBeanClass = PredicateContextAwareJpaFactoryBean.class)
@Import(OpenRestFiltersConfiguration.class)
public @interface EnableOpenRestFilters {

}
