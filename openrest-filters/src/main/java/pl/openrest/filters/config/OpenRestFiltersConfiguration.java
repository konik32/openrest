package pl.openrest.filters.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.webmvc.BaseUri;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.plugin.core.PluginRegistry;

import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
import pl.openrest.filters.predicate.PredicateRepositoryFactory;
import pl.openrest.filters.query.PredicateContextBuilderFactory;
import pl.openrest.filters.webmvc.FiltersPredicateRepositoryInvokerResolver;
import pl.openrest.filters.webmvc.IdRepositoryInvokerResolver;
import pl.openrest.filters.webmvc.PredicateContextResolver;
import pl.openrest.filters.webmvc.SearchPredicateRepositoryInvokerResolver;
import pl.openrest.predicate.parser.DefaultFilterTreeBuilder;
import pl.openrest.predicate.parser.DefaultPredicatePartsExtractor;
import pl.openrest.predicate.parser.FilterTreeBuilder;
import pl.openrest.predicate.parser.PredicatePartsExtractor;

@Configuration
public class OpenRestFiltersConfiguration {

    @Autowired
    private Repositories repositories;

    @Autowired
    private PredicateContextBuilderFactory<?> predicateContextBuilderFactory;

    @Autowired
    private PredicateRepositoryFactory predicateRepositoryFactory;

    @Autowired
    private FilterableEntityRegistry filterableEntityRegistry;

    @Autowired
    private PredicateContextResolver predicateContextResolver;
    @Autowired
    private PluginRegistry<BackendIdConverter, Class<?>> idConverters;
    @Autowired
    private BaseUri baseUri;

    @Bean
    public IdRepositoryInvokerResolver idRepositoryResolver() {
        return new IdRepositoryInvokerResolver(filterableEntityRegistry, predicateContextResolver, idConverters, baseUri);
    }

    @Bean
    public SearchPredicateRepositoryInvokerResolver searchPredicateRepositoryInvokerResolver() {
        return new SearchPredicateRepositoryInvokerResolver(filterableEntityRegistry, predicateContextResolver);
    }

    @Bean
    @Order(value=Ordered.LOWEST_PRECEDENCE)
    public FiltersPredicateRepositoryInvokerResolver filtersPredicateRepositoryInvokerResolver() {
        return new FiltersPredicateRepositoryInvokerResolver(filterableEntityRegistry, predicateContextResolver);
    }

    @Bean
    public FilterableEntityRegistry filterableEntityRegistry() {
        return new FilterableEntityRegistry(repositories, predicateRepositoryFactory);
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
    public PredicateContextResolver filterableEntityResolver() {
        return new PredicateContextResolver(predicateContextBuilderFactory, filterTreeBuilder(), predicatePartsExtractor());
    }

}
