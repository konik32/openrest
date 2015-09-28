package pl.openrest.dto.webmvc;

/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import lombok.NonNull;

import org.springframework.core.MethodParameter;
import org.springframework.data.rest.core.invoke.RepositoryInvoker;
import org.springframework.data.rest.webmvc.IncomingRequest;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.config.RootResourceInformationHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.support.BackendIdHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import pl.openrest.dto.mapper.MappingManager;
import pl.openrest.dto.mapper.MappingManager.DtoAndEntityWrapper;

/**
 * Custom {@link HandlerMethodArgumentResolver} to create
 * {@link PersistentEntityResource} instances.
 * 
 * @author Szymon Konicki
 */
public class DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	public static final String DTO_PARAM_NAME = "dto";
	private final RootResourceInformationHandlerMethodArgumentResolver resourceInformationResolver;

	private final BackendIdHandlerMethodArgumentResolver idResolver;
	private final MappingManager mappingManager;

	public DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver(
			@NonNull RootResourceInformationHandlerMethodArgumentResolver resourceInformationResolver,
			@NonNull BackendIdHandlerMethodArgumentResolver idResolver,
			@NonNull MappingManager mappingManager) {
		this.resourceInformationResolver = resourceInformationResolver;
		this.idResolver = idResolver;
		this.mappingManager = mappingManager;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return PersistentEntityResourceWithDtoWrapper.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		String dtoParam = webRequest.getParameter(DTO_PARAM_NAME);

		RootResourceInformation resourceInformation = resourceInformationResolver.resolveArgument(parameter,
				mavContainer, webRequest, binderFactory);

		HttpServletRequest nativeRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		ServletServerHttpRequest request = new ServletServerHttpRequest(nativeRequest);
		IncomingRequest incoming = new IncomingRequest(request);

		MediaType contentType = request.getHeaders().getContentType();

		Serializable id = idResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
		DtoAndEntityWrapper wrapper;
		if (incoming.isPatchRequest()) {
			RepositoryInvoker invoker = resourceInformation.getInvoker();
			wrapper = mappingManager.merge(contentType, incoming.getServerHttpRequest(), invoker, id, dtoParam);
		} else {
			wrapper = mappingManager.create(contentType, incoming.getServerHttpRequest(), dtoParam);
		}

		PersistentEntityResource entityResource = PersistentEntityResource.build(wrapper.getEntity(),
				resourceInformation.getPersistentEntity()).build();

		return new PersistentEntityResourceWithDtoWrapper(entityResource, wrapper.getDto());
	}

}
