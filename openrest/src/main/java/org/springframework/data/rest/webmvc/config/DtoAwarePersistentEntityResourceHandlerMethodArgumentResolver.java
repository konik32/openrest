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
import orest.dto.DefaultEntityFromDtoCreator;
import orest.dto.DtoDomainRegistry;
import orest.dto.DtoInformation;
import orest.expression.SpelEvaluatorBean;
import orest.security.ExpressionEvaluator;
import orest.validation.UpdateValidationContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.rest.core.invoke.RepositoryInvoker;
import org.springframework.data.rest.webmvc.IncomingRequest;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.json.DomainObjectReader;
import org.springframework.data.rest.webmvc.support.BackendIdHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom {@link HandlerMethodArgumentResolver} to create
 * {@link PersistentEntityResource} instances.
 * 
 * @author Jon Brisbin
 * @author Oliver Gierke
 * @author Szymon Konicki
 */
public class DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver extends PersistentEntityResourceHandlerMethodArgumentResolver {

	private static final String ERROR_MESSAGE = "Could not read an object of type %s from the request!";
	private static final String NO_CONVERTER_FOUND = "No suitable HttpMessageConverter found to read request body into object of type %s from request with content type of %s!";
	public static final String DTO_PARAM_NAME = "dto";
	private final RootResourceInformationHandlerMethodArgumentResolver resourceInformationResolver;

	private final BackendIdHandlerMethodArgumentResolver idResolver;
	private final DomainObjectReader reader;
	private final List<HttpMessageConverter<?>> messageConverters;
	private final DtoDomainRegistry dtoDomainRegistry;
	private final DefaultEntityFromDtoCreator entityFromDtoCreator;
	private SpelEvaluatorBean spelEvaluator;
	private Validator validator;
	@Autowired
	private UpdateValidationContext updateValidationContext;
	@Autowired
	private @Setter ExpressionEvaluator expressionEvaluator;
	
	private boolean validate = true;

	public DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters,
			RootResourceInformationHandlerMethodArgumentResolver resourceInformationResolver, BackendIdHandlerMethodArgumentResolver idResolver,
			DomainObjectReader reader, DtoDomainRegistry dtoDomainRegistry, DefaultEntityFromDtoCreator entityFromDtoCreator) {
		super(messageConverters, resourceInformationResolver, idResolver, reader);
		this.messageConverters = messageConverters;
		this.resourceInformationResolver = resourceInformationResolver;
		this.idResolver = idResolver;
		this.reader = reader;

		Assert.notNull(dtoDomainRegistry);
		Assert.notNull(entityFromDtoCreator);
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
		return PersistentEntityResource.class.isAssignableFrom(parameter.getParameterType());
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
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
			throws Exception {

		RootResourceInformation resourceInformation = resourceInformationResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

		HttpServletRequest nativeRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		ServletServerHttpRequest request = new ServletServerHttpRequest(nativeRequest);
		IncomingRequest incoming = new IncomingRequest(request);

		Class<?> domainType = resourceInformation.getDomainType();
		MediaType contentType = request.getHeaders().getContentType();

		Class<?> dtoType = null;
		DtoInformation dtoInfo = null;
		// if (!incoming.isPatchRequest()) {
		String dtoParam = webRequest.getParameter(DTO_PARAM_NAME);
		if (StringUtils.hasText(dtoParam)) {
			dtoInfo = dtoDomainRegistry.get(dtoParam);
			if (dtoInfo != null) {
				authorizeDto(dtoInfo);
				dtoType = dtoInfo.getDtoType();
			}
		}
		// }
		for (HttpMessageConverter converter : messageConverters) {

			if (!converter.canRead(PersistentEntityResource.class, contentType)) {
				continue;
			}

			Serializable id = idResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
			Object obj = read(resourceInformation, incoming, converter, id, dtoType);

			if (dtoInfo != null) {
				evaluateSpelExpressions(obj);
//				validate(obj);
				if (incoming.isPatchRequest()) {
					RepositoryInvoker invoker = resourceInformation.getInvoker();
					Object existingObject = invoker.invokeFindOne(id);
					validate(obj, existingObject);
					if (existingObject == null) {
						throw new ResourceNotFoundException();
					}
					
					entityFromDtoCreator.merge(obj, existingObject, dtoInfo);
					obj = existingObject;
				} else {
					validate(obj);
					obj = get(obj, dtoInfo);
					
				}
			}
			if (obj == null) {
				throw new HttpMessageNotReadableException(String.format(ERROR_MESSAGE, domainType));
			}

			return PersistentEntityResource.build(obj, resourceInformation.getPersistentEntity()).build();
		}

		throw new HttpMessageNotReadableException(String.format(NO_CONVERTER_FOUND, domainType, contentType));
	}

	/**
	 * Reads the given {@link ServerHttpRequest} into an object of the type of
	 * the given {@link RootResourceInformation}, potentially applying the
	 * content to an object of the given id.
	 * 
	 * @param information
	 * @param request
	 * @param converter
	 * @param id
	 * @return
	 * @throws IOException
	 */
	private Object read(RootResourceInformation information, IncomingRequest request, HttpMessageConverter<Object> converter, Serializable id, Class<?> type) {

		if (type == null && request.isPatchRequest() && converter instanceof MappingJackson2HttpMessageConverter) {

			if (id == null) {
				new ResourceNotFoundException();
			}

			RepositoryInvoker invoker = information.getInvoker();
			Object existingObject = invoker.invokeFindOne(id);

			if (existingObject == null) {
				throw new ResourceNotFoundException();
			}

			ObjectMapper mapper = ((MappingJackson2HttpMessageConverter) converter).getObjectMapper();
			Object result = readPatch(request, mapper, existingObject);

			return result;
		}

		return read(request, converter, information, type);
	}

	private Object readPatch(IncomingRequest request, ObjectMapper mapper, Object existingObject) {

		try {
			JsonPatchHandler handler = new JsonPatchHandler(mapper, reader);
			return handler.apply(request, existingObject);
		} catch (Exception e) {
			throw new HttpMessageNotReadableException(String.format(ERROR_MESSAGE, existingObject.getClass()));
		}
	}

	private Object read(IncomingRequest request, HttpMessageConverter<Object> converter, RootResourceInformation information, Class<?> type) {

		try {
			if (type != null)
				return converter.read(type, request.getServerHttpRequest());
			else
				return converter.read(information.getDomainType(), request.getServerHttpRequest());
		} catch (IOException e) {
			throw new HttpMessageNotReadableException(String.format(ERROR_MESSAGE, information.getDomainType()));
		}
	}

	private void evaluateSpelExpressions(Object object) {
		if (spelEvaluator != null)
			spelEvaluator.evaluate(object);
	}

	private Object get(Object obj, DtoInformation dtoInfo) {
		return entityFromDtoCreator.create(obj, dtoInfo);
	}

	private void validate(Object from) {
		if (!validate || validator == null)
			return;
		Set<ConstraintViolation<Object>> violations = validator.validate(from);
		if (violations.size() > 0) {
			ConstraintViolation<Object> violation = violations.iterator().next();
			throw new ConstraintViolationException(violation.getPropertyPath() + " " + violation.getMessage(), violations);
		}

	}
	
	private void validate(Object dto, Object entity){
		updateValidationContext.setDto(dto);
		updateValidationContext.setEntity(entity);
		validate(dto);
	}
	
	private void authorizeDto(DtoInformation dtoInfo) {
		if (dtoInfo.getAuthorizationCondition() != null && expressionEvaluator != null)
			if (!expressionEvaluator.checkCondition(dtoInfo.getAuthorizationCondition()))
				throw new AccessDeniedException("You are not authorized to use this dto: " + dtoInfo.getName());
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public void setSpelEvaluatorBean(SpelEvaluatorBean spelEvaluatorBean) {
		this.spelEvaluator = spelEvaluatorBean;
	}

}
