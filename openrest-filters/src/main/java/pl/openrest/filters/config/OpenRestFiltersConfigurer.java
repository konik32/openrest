package pl.openrest.filters.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.webmvc.BaseUri;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import pl.openrest.core.config.OpenRestConfigurer;
import pl.openrest.core.webmvc.RepositoryInvokerResolver;
import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
import pl.openrest.filters.webmvc.FiltersPredicateRepositoryInvokerResolver;
import pl.openrest.filters.webmvc.IdRepositoryInvokerResolver;
import pl.openrest.filters.webmvc.PredicateContextResolver;
import pl.openrest.filters.webmvc.SearchPredicateRepositoryInvokerResolver;

public class OpenRestFiltersConfigurer implements OpenRestConfigurer {

    @Autowired
    private FilterableEntityRegistry filterableEntityRegistry;

    @Autowired
    private PredicateContextResolver predicateContextResolver;
    @Autowired
    private PluginRegistry<BackendIdConverter, Class<?>> idConverters;
    @Autowired
    private BaseUri baseUri;

    @Override
    public void addRepositoryInvokerResolvers(List<RepositoryInvokerResolver> resolvers) {
        resolvers.add(new IdRepositoryInvokerResolver(filterableEntityRegistry, predicateContextResolver, idConverters, baseUri));
        resolvers.add(new SearchPredicateRepositoryInvokerResolver(filterableEntityRegistry, predicateContextResolver));
        resolvers.add(new FiltersPredicateRepositoryInvokerResolver(filterableEntityRegistry, predicateContextResolver));
    }

    @Override
    public void addDefaultMethodArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // TODO Auto-generated method stub
    }

}
