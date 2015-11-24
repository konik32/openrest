package pl.openrest.filters.querydsl.webmvc;

import org.springframework.core.MethodParameter;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.DefaultedPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.querydsl.webmvc.support.PageAndSortUtils;
import pl.openrest.filters.webmvc.FilterableEntityInformationMethodArgumentResolver;

public class DefaultedQPageableHandlerMethodArgumentResolver extends DefaultedPageableHandlerMethodArgumentResolver {

    private final PageAndSortUtils pageAndSortUtils;

    private final FilterableEntityInformationMethodArgumentResolver filterableEntityInformationMethodArgumentResolver;

    public DefaultedQPageableHandlerMethodArgumentResolver(PageableHandlerMethodArgumentResolver resolver,
            PageAndSortUtils pageAndSortUtils,
            FilterableEntityInformationMethodArgumentResolver filterableEntityInformationMethodArgumentResolver) {
        super(resolver);
        this.pageAndSortUtils = pageAndSortUtils;
        this.filterableEntityInformationMethodArgumentResolver = filterableEntityInformationMethodArgumentResolver;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        DefaultedPageable defaultedPageable = (DefaultedPageable) super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        FilterableEntityInformation entityInfo = filterableEntityInformationMethodArgumentResolver.resolveArgument(parameter, mavContainer,
                webRequest, binderFactory);
        return new DefaultedPageable(pageAndSortUtils.toQPageRequest(defaultedPageable.getPageable(), entityInfo),
                defaultedPageable.isDefault());
    }

}
