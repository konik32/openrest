package pl.openrest.filters.config;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import pl.openrest.core.config.OpenRestConfigurer;
import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
import pl.openrest.filters.webmvc.DefaultedQPageableHandlerMethodArgumentResolver;
import pl.openrest.filters.webmvc.FilterableEntityInformationMethodArgumentResolver;
import pl.openrest.filters.webmvc.QSortMethodArgumentResolver;
import pl.openrest.filters.webmvc.support.PageAndSortUtils;

public class OpenRestFiltersConfigurer implements OpenRestConfigurer {

    @Autowired
    private FilterableEntityRegistry filterableEntityRegistry;

    @Autowired
    private PageAndSortUtils pageAndSortUtils;

    @Override
    public void addDefaultMethodArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver = null;
        HateoasPageableHandlerMethodArgumentResolver pageableResolver = null;
        HateoasSortHandlerMethodArgumentResolver sortResolver = null;
        Iterator<HandlerMethodArgumentResolver> it = resolvers.iterator();
        while (it.hasNext()) {
            HandlerMethodArgumentResolver resolver = it.next();
            if (resolver instanceof ResourceMetadataHandlerMethodArgumentResolver)
                resourceMetadataResolver = (ResourceMetadataHandlerMethodArgumentResolver) resolver;
            else if (resolver instanceof HateoasPageableHandlerMethodArgumentResolver) {
                pageableResolver = (HateoasPageableHandlerMethodArgumentResolver) resolver;
                it.remove();
            } else if (resolver instanceof HateoasSortHandlerMethodArgumentResolver) {
                sortResolver = (HateoasSortHandlerMethodArgumentResolver) resolver;
            }
        }
        FilterableEntityInformationMethodArgumentResolver filterableEntityResolver = new FilterableEntityInformationMethodArgumentResolver(
                resourceMetadataResolver, filterableEntityRegistry);
        resolvers.add(new DefaultedQPageableHandlerMethodArgumentResolver(pageableResolver, pageAndSortUtils, filterableEntityResolver));
        resolvers.add(new QSortMethodArgumentResolver(sortResolver, pageAndSortUtils, filterableEntityResolver));
        resolvers.add(filterableEntityResolver);
    }
}
