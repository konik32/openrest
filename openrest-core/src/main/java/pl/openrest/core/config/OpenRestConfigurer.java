package pl.openrest.core.config;

import java.util.List;

import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.config.RootResourceInformationHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.support.BackendIdHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

public interface OpenRestConfigurer {

    public void addDefaultMethodArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers, RootResourceInformationHandlerMethodArgumentResolver rootResourceResolver, BackendIdHandlerMethodArgumentResolver backendIdResolver, ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver);

}
