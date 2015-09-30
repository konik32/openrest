package pl.openrest.filters.webmvc;

import lombok.NonNull;

import org.springframework.core.MethodParameter;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.domain.registry.FilterableEntityRegistry;

public class FilterableEntityInformationMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver;
    private final FilterableEntityRegistry filterableEntityRegistry;

    public FilterableEntityInformationMethodArgumentResolver(
            @NonNull ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver,
            @NonNull FilterableEntityRegistry filterableEntityRegistry) {
        this.resourceMetadataResolver = resourceMetadataResolver;
        this.filterableEntityRegistry = filterableEntityRegistry;
    }

    @Override
    public FilterableEntityInformation resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        ResourceMetadata metadata = resourceMetadataResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        FilterableEntityInformation entityInfo = filterableEntityRegistry.get(metadata.getDomainType());
        if (entityInfo == null)
            throw new IllegalArgumentException("Could not resolve FilterableEntityInformation");
        return entityInfo;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return FilterableEntityInformation.class.isAssignableFrom(parameter.getParameterType());
    }

}
