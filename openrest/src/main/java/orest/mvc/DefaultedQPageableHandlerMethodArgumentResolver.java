package orest.mvc;

import orest.expression.registry.EntityExpressionMethodsRegistry;
import orest.expression.registry.ExpressionEntityInformation;
import orest.webmvc.support.PageAndSortUtils;

import org.springframework.core.MethodParameter;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.DefaultedPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class DefaultedQPageableHandlerMethodArgumentResolver extends DefaultedPageableHandlerMethodArgumentResolver {

	private final PageAndSortUtils pageAndSortUtils;

	private final ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver;
	private final EntityExpressionMethodsRegistry entityExpressionMethodsRegistry;

	public DefaultedQPageableHandlerMethodArgumentResolver(PageableHandlerMethodArgumentResolver resolver,
			PageAndSortUtils pageAndSortUtils, ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver,
			EntityExpressionMethodsRegistry entityExpressionMethodsRegistry) {
		super(resolver);
		this.pageAndSortUtils = pageAndSortUtils;
		this.resourceMetadataResolver = resourceMetadataResolver;
		this.entityExpressionMethodsRegistry = entityExpressionMethodsRegistry;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		DefaultedPageable defaultedPageable = (DefaultedPageable) super.resolveArgument(parameter, mavContainer,
				webRequest, binderFactory);
		ResourceMetadata metadata = resourceMetadataResolver.resolveArgument(parameter, mavContainer, webRequest,
				binderFactory);
		ExpressionEntityInformation entityInfo = entityExpressionMethodsRegistry.getEntityInformation(metadata
				.getDomainType());
		return new DefaultedPageable(pageAndSortUtils.toQPageRequest(defaultedPageable.getPageable(), entityInfo),
				defaultedPageable.isDefault());
	}

}
