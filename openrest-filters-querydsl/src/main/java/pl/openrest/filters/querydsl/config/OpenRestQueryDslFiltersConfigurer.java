package pl.openrest.filters.querydsl.config;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import pl.openrest.core.config.OpenRestConfigurer;
import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
import pl.openrest.filters.querydsl.webmvc.DefaultedQPageableHandlerMethodArgumentResolver;
import pl.openrest.filters.querydsl.webmvc.QSortMethodArgumentResolver;
import pl.openrest.filters.querydsl.webmvc.support.PageAndSortUtils;
import pl.openrest.filters.webmvc.PredicateContextResolver;

public class OpenRestQueryDslFiltersConfigurer implements OpenRestConfigurer {

    @Autowired
    private FilterableEntityRegistry filterableEntityRegistry;

    @Autowired
    private PageAndSortUtils pageAndSortUtils;
    @Autowired
    private Repositories repositories;
    @Autowired
    private PredicateContextResolver predicateContextResolver;
    @Autowired
    private RepositoryInvokerFactory invokerFactory;

    @Override
    public void addDefaultMethodArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver = null;
        HateoasPageableHandlerMethodArgumentResolver pageableResolver = null;
        HateoasSortHandlerMethodArgumentResolver sortResolver = null;
        Iterator<HandlerMethodArgumentResolver> it = resolvers.iterator();
        while (it.hasNext()) {
            HandlerMethodArgumentResolver resolver = it.next();
            if (resolver instanceof ResourceMetadataHandlerMethodArgumentResolver) {
                resourceMetadataResolver = (ResourceMetadataHandlerMethodArgumentResolver) resolver;
            } else if (resolver instanceof HateoasPageableHandlerMethodArgumentResolver) {
                pageableResolver = (HateoasPageableHandlerMethodArgumentResolver) resolver;
            } else if (resolver instanceof HateoasSortHandlerMethodArgumentResolver) {
                sortResolver = (HateoasSortHandlerMethodArgumentResolver) resolver;
            }
        }
        resolvers.add(0, new DefaultedQPageableHandlerMethodArgumentResolver(pageableResolver, pageAndSortUtils, filterableEntityRegistry,
                resourceMetadataResolver));
        resolvers.add(0,
                new QSortMethodArgumentResolver(sortResolver, pageAndSortUtils, filterableEntityRegistry, resourceMetadataResolver));
    }

}
