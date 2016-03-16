package pl.openrest.filters.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.Repositories;

import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
import pl.openrest.filters.predicate.PredicateRepositoryFactory;
import pl.openrest.filters.query.PredicateContextBuilderFactory;
import pl.openrest.filters.webmvc.PredicateContextResolver;
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

	@Bean
	public FilterableEntityRegistry filterableEntityRegistry() {
		return new FilterableEntityRegistry(repositories,
				predicateRepositoryFactory);
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
		return new PredicateContextResolver(predicateContextBuilderFactory,
				filterTreeBuilder(), predicatePartsExtractor());
	}
	
    @Bean
    public OpenRestFiltersConfigurer openRestFiltersConfigurer() {
        return new OpenRestFiltersConfigurer();
    }

}
