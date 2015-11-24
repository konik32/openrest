package pl.openrest.filters.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.repository.support.Repositories;

import pl.openrest.core.config.OpenRestConfigurer;
import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
import pl.openrest.filters.predicate.ConversionServiceBasedIdConverter;
import pl.openrest.filters.predicate.ConversionServiceBasedMethodParameterConverter;
import pl.openrest.filters.predicate.IdConverter;
import pl.openrest.filters.predicate.MethodParameterConverter;
import pl.openrest.filters.predicate.SpelMethodParameterConverter;
import pl.openrest.filters.query.PredicateContextBuilderFactory;
import pl.openrest.filters.webmvc.FilterableEntityResolver;
import pl.openrest.predicate.parser.DefaultFilterTreeBuilder;
import pl.openrest.predicate.parser.DefaultPredicatePartsExtractor;
import pl.openrest.predicate.parser.FilterTreeBuilder;
import pl.openrest.predicate.parser.PredicatePartsExtractor;

@Configuration
public class OpenRestFiltersConfiguration {

    @Autowired
    private Repositories repositories;

    // @Autowired
    // private ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver;

    @Autowired
    private PredicateContextBuilderFactory<?> predicateContextBuilderFactory;

    @Autowired
    private ConversionService defaultConversionService;

    @Autowired
    private BeanFactory beanFactory;

    @Bean
    public FilterableEntityRegistry filterableEntityRegistry() {
        return new FilterableEntityRegistry(repositories);
    }

    protected MethodParameterConverter predicateParameterConverter() {
        return new ConversionServiceBasedMethodParameterConverter(defaultConversionService);
    }

    protected MethodParameterConverter staticFiltersParameterConverter() {
        return new SpelMethodParameterConverter(beanFactory);
    }

    protected IdConverter idConverter() {
        return new ConversionServiceBasedIdConverter(defaultConversionService);
    }

    @Bean
    public FilterTreeBuilder filterTreeBuilder() {
        return new DefaultFilterTreeBuilder(predicatePartsExtractor());
    }

    @Bean
    public PredicatePartsExtractor predicatePartsExtractor() {
        return new DefaultPredicatePartsExtractor();
    }

    @Bean
    public OpenRestConfigurer openRestFiltersConfigurer() {
        return new OpenRestFiltersConfigurer();
    }

    @Bean
    public FilterableEntityResolver filterableEntityResolver() {
        return new FilterableEntityResolver(predicateContextBuilderFactory, filterableEntityRegistry(), filterTreeBuilder(),
                predicatePartsExtractor());
    }

}
