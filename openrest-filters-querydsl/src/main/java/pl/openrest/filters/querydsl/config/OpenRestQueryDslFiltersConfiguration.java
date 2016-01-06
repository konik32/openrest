package pl.openrest.filters.querydsl.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

import pl.openrest.filters.predicate.ConversionServiceBasedIdConverter;
import pl.openrest.filters.predicate.ConversionServiceBasedMethodParameterConverter;
import pl.openrest.filters.predicate.IdConverter;
import pl.openrest.filters.predicate.MethodParameterConverter;
import pl.openrest.filters.predicate.PredicateRepositoryFactory;
import pl.openrest.filters.predicate.SpelMethodParameterConverter;
import pl.openrest.filters.querydsl.predicate.QPredicateRepositoryFactory;
import pl.openrest.filters.querydsl.query.QPredicateContextBuilderFactory;
import pl.openrest.filters.querydsl.webmvc.support.PageAndSortUtils;
import pl.openrest.predicate.parser.PredicatePartsExtractor;

@Configuration
public class OpenRestQueryDslFiltersConfiguration {
    @Autowired
    private ConversionService defaultConversionService;

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private PredicatePartsExtractor predicatePartsExtractor;

    @Bean
    public MethodParameterConverter predicateParameterConverter() {
        return new ConversionServiceBasedMethodParameterConverter(defaultConversionService);
    }

    @Bean
    public MethodParameterConverter staticFiltersParameterConverter() {
        return new SpelMethodParameterConverter(beanFactory);
    }

    @Bean
    public IdConverter idConverter() {
        return new ConversionServiceBasedIdConverter(defaultConversionService);
    }

    @Bean
    public QPredicateContextBuilderFactory predicateContextBuilderFactory() {
        return new QPredicateContextBuilderFactory(predicateParameterConverter(), staticFiltersParameterConverter(), idConverter());
    }

    @Bean
    public PageAndSortUtils pageAndSortUtils() {
        return new PageAndSortUtils(predicateContextBuilderFactory(), predicatePartsExtractor);
    }

    @Bean
    public OpenRestQueryDslFiltersConfigurer openRestQueryDslFiltersConfigurer() {
        return new OpenRestQueryDslFiltersConfigurer();
    }

    @Bean
    public PredicateRepositoryFactory qPredicateRepositoryFactory() {
        return new QPredicateRepositoryFactory();
    }
}
