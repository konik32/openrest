package pl.openrest.filters.querydsl.webmvc;

import lombok.RequiredArgsConstructor;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
import pl.openrest.filters.querydsl.webmvc.support.PageAndSortUtils;

@RequiredArgsConstructor
public class QSortMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final SortHandlerMethodArgumentResolver sortResolver;
    private final PageAndSortUtils pageAndSortUtils;
    private final FilterableEntityRegistry filterableEntityRegistry;
    private final ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return QSort.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        Sort sort = sortResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        ResourceMetadata resourceMetadata = resourceMetadataResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        FilterableEntityInformation entityInfo = filterableEntityRegistry.get(resourceMetadata.getDomainType());
        return pageAndSortUtils.toQSort(sort, entityInfo);
    }

}
