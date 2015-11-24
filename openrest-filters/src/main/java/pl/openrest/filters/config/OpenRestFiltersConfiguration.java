package pl.openrest.filters.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.Repositories;

import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
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

    @Autowired
    private PredicateContextBuilderFactory<?> predicateContextBuilderFactory;

    @Bean
    public FilterableEntityRegistry filterableEntityRegistry() {
        return new FilterableEntityRegistry(repositories);
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
    public FilterableEntityResolver filterableEntityResolver() {
        return new FilterableEntityResolver(predicateContextBuilderFactory, filterableEntityRegistry(), filterTreeBuilder(),
                predicatePartsExtractor());
    }

}
