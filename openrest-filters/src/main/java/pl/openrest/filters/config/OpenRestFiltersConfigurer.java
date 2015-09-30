package pl.openrest.filters.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.config.RootResourceInformationHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.support.BackendIdHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import pl.openrest.core.config.OpenRestConfigurer;
import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
import pl.openrest.filters.webmvc.FilterableEntityInformationMethodArgumentResolver;

public class OpenRestFiltersConfigurer implements OpenRestConfigurer {

    @Autowired
    private FilterableEntityRegistry filterableEntityRegistry;

    @Override
    public void addDefaultMethodArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers,
            RootResourceInformationHandlerMethodArgumentResolver rootResourceResolver,
            BackendIdHandlerMethodArgumentResolver backendIdResolver, ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver) {
        resolvers.add(new FilterableEntityInformationMethodArgumentResolver(resourceMetadataResolver, filterableEntityRegistry));
    }
}
