package orest.mvc;

import lombok.RequiredArgsConstructor;
import orest.expression.registry.EntityExpressionMethodsRegistry;
import orest.expression.registry.ExpressionEntityInformation;
import orest.webmvc.support.PageAndSortUtils;

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

@RequiredArgsConstructor
public class QSortMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private final SortHandlerMethodArgumentResolver sortResolver;
	private final PageAndSortUtils pageAndSortUtils;
	private final ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver;
	private final EntityExpressionMethodsRegistry entityExpressionMethodsRegistry;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return QSort.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Sort sort = sortResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
		ResourceMetadata metadata = resourceMetadataResolver.resolveArgument(parameter, mavContainer, webRequest,
				binderFactory);
		ExpressionEntityInformation entityInfo = entityExpressionMethodsRegistry.getEntityInformation(metadata
				.getDomainType());
		return pageAndSortUtils.toQSort(sort, entityInfo);
	}

}
