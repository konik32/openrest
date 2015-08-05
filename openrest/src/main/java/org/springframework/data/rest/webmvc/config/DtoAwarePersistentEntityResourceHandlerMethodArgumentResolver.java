package org.springframework.data.rest.webmvc.config;

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

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import lombok.Setter;
import orest.dto.DefaultEntityFromDtoCreatorAndMerger;
import orest.dto.DtoDomainRegistry;
import orest.dto.DtoInformation;
import orest.dto.authorization.SpringSecurityAuthorizationStrategyDtoHandler;
import orest.dto.expression.spel.DtoEvaluationWrapper;
import orest.dto.expression.spel.SpelEvaluatorBean;
import orest.dto.handler.DtoHandlerManager;
import orest.dto.validation.UpdateValidationContext;
import orest.exception.OrestException;
import orest.exception.OrestExceptionDictionary;
import orest.security.ExpressionEvaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.rest.core.invoke.RepositoryInvoker;
import org.springframework.data.rest.webmvc.IncomingRequest;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.support.BackendIdHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Custom {@link HandlerMethodArgumentResolver} to create
 * {@link PersistentEntityResource} instances.
 * 
 * @author Szymon Konicki
 */
public class DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String ERROR_MESSAGE = "Could not read an object of type %s from the request!";
	private static final String NO_CONVERTER_FOUND = "No suitable HttpMessageConverter found to read request body into object of type %s from request with content type of %s!";
	public static final String DTO_PARAM_NAME = "dto";
	private final RootResourceInformationHandlerMethodArgumentResolver resourceInformationResolver;

	private final BackendIdHandlerMethodArgumentResolver idResolver;
	private final List<HttpMessageConverter<?>> messageConverters;
	private final DtoDomainRegistry dtoDomainRegistry;
	private final DefaultEntityFromDtoCreatorAndMerger entityFromDtoCreator;

	private @Setter DtoHandlerManager dtoHandlerManager;

	public DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver(
			List<HttpMessageConverter<?>> messageConverters,
			RootResourceInformationHandlerMethodArgumentResolver resourceInformationResolver,
			BackendIdHandlerMethodArgumentResolver idResolver, DtoDomainRegistry dtoDomainRegistry,
			DefaultEntityFromDtoCreatorAndMerger entityFromDtoCreator) {
		Assert.notNull(dtoDomainRegistry);
		Assert.notNull(entityFromDtoCreator);
		this.messageConverters = messageConverters;
		this.resourceInformationResolver = resourceInformationResolver;
		this.idResolver = idResolver;
		this.dtoDomainRegistry = dtoDomainRegistry;
		this.entityFromDtoCreator = entityFromDtoCreator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.method.support.HandlerMethodArgumentResolver#
	 * supportsParameter(org.springframework.core.MethodParameter)
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return PersistentEntityResourceWithDtoWrapper.class.isAssignableFrom(parameter.getParameterType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.method.support.HandlerMethodArgumentResolver#
	 * resolveArgument(org.springframework.core.MethodParameter,
	 * org.springframework.web.method.support.ModelAndViewContainer,
	 * org.springframework.web.context.request.NativeWebRequest,
	 * org.springframework.web.bind.support.WebDataBinderFactory)
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		// verify that dto param exists and contains valid dto name
		String dtoParam = webRequest.getParameter(DTO_PARAM_NAME);
		if (!StringUtils.hasText(dtoParam))
			throw new OrestException(OrestExceptionDictionary.NO_DTO_PARAMETER, "Request must contain dto parameter");
		DtoInformation dtoInfo = dtoDomainRegistry.get(dtoParam);
		if (dtoInfo == null)
			throw new OrestException(OrestExceptionDictionary.NO_SUCH_DTO, "There is no such dto: " + dtoParam
					+ " defined");

		RootResourceInformation resourceInformation = resourceInformationResolver.resolveArgument(parameter,
				mavContainer, webRequest, binderFactory);

		HttpServletRequest nativeRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		ServletServerHttpRequest request = new ServletServerHttpRequest(nativeRequest);
		IncomingRequest incoming = new IncomingRequest(request);

		Class<?> domainType = resourceInformation.getDomainType();
		MediaType contentType = request.getHeaders().getContentType();

		Class<?> dtoType = dtoInfo.getDtoType();

		for (HttpMessageConverter converter : messageConverters) {

			if (!converter.canRead(PersistentEntityResource.class, contentType)) {
				continue;
			}

			Serializable id = idResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
			Object dto = read(incoming, converter, dtoType);
			Object entity = null;
			// inject @Value's in dto

			if (incoming.isPatchRequest()) {
				RepositoryInvoker invoker = resourceInformation.getInvoker();
				Object existingObject = invoker.invokeFindOne(id);
				if (existingObject == null) {
					throw new ResourceNotFoundException();
				}
				dtoHandlerManager.handle(dto, entity);
				// merge entity with dto
				entityFromDtoCreator.merge(dto, existingObject, dtoInfo);
				entity = existingObject;
			} else {
				dtoHandlerManager.handle(dto, entity);
				// create entity from dto
				entity = entityFromDtoCreator.create(dto, dtoInfo);
			}
			if (entity == null) {
				throw new HttpMessageNotReadableException(String.format(ERROR_MESSAGE, domainType));
			}

			PersistentEntityResource entityResource = PersistentEntityResource.build(entity,
					resourceInformation.getPersistentEntity()).build();

			return new PersistentEntityResourceWithDtoWrapper(entityResource, dto);
		}

		throw new HttpMessageNotReadableException(String.format(NO_CONVERTER_FOUND, domainType, contentType));
	}

	/**
	 * Reads the given {@link ServerHttpRequest} into an object of the type of
	 * the given dto, potentially applying the content to an object of the given
	 * id.
	 * 
	 * @param request
	 * @param converter
	 * @param information
	 * @param type
	 * @return
	 * @throws HttpMessageNotReadableException
	 */
	private Object read(IncomingRequest request, HttpMessageConverter<Object> converter, Class<?> type) {
		try {
			return converter.read(type, request.getServerHttpRequest());
		} catch (IOException e) {
			throw new HttpMessageNotReadableException(String.format(ERROR_MESSAGE, type));
		}
	}

}
