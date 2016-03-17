package pl.openrest.filters.querydsl.webmvc;

import org.springframework.core.MethodParameter;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.DefaultedPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
import pl.openrest.filters.querydsl.webmvc.support.PageAndSortUtils;

public class DefaultedQPageableHandlerMethodArgumentResolver extends DefaultedPageableHandlerMethodArgumentResolver{

    private final PageAndSortUtils pageAndSortUtils;

    private final FilterableEntityRegistry filterableEntityRegistry;
    private final ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver;

    public DefaultedQPageableHandlerMethodArgumentResolver(PageableHandlerMethodArgumentResolver resolver,
            PageAndSortUtils pageAndSortUtils, FilterableEntityRegistry filterableEntityRegistry,
            ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver) {
        super(resolver);
        this.pageAndSortUtils = pageAndSortUtils;
        this.filterableEntityRegistry = filterableEntityRegistry;
        this.resourceMetadataResolver = resourceMetadataResolver;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        DefaultedPageable defaultedPageable = (DefaultedPageable) super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        ResourceMetadata resourceMetadata = resourceMetadataResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        FilterableEntityInformation entityInfo = filterableEntityRegistry.get(resourceMetadata.getDomainType());
        QDefaultedPageRequest pageRequest = pageAndSortUtils.toQPageRequest(defaultedPageable.getPageable(), entityInfo);
        pageRequest.setDefault(defaultedPageable.isDefault());
        return new DefaultedPageable(pageRequest, defaultedPageable.isDefault());
    }

}
