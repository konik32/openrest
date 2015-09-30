package pl.openrest.filters.webmvc;

import lombok.RequiredArgsConstructor;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.webmvc.support.PageAndSortUtils;

@RequiredArgsConstructor
public class QSortMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final SortHandlerMethodArgumentResolver sortResolver;
    private final PageAndSortUtils pageAndSortUtils;
    private final FilterableEntityInformationMethodArgumentResolver filterableEntityInformationMethodArgumentResolver;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return QSort.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        Sort sort = sortResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        FilterableEntityInformation entityInfo = filterableEntityInformationMethodArgumentResolver.resolveArgument(parameter, mavContainer,
                webRequest, binderFactory);
        return pageAndSortUtils.toQSort(sort, entityInfo);
    }

}
