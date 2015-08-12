package orest.dto;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NonNull;
import orest.dto.handler.DtoHandler;
import orest.exception.OrestException;
import orest.exception.OrestExceptionDictionary;

import org.springframework.data.rest.core.invoke.RepositoryInvoker;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.util.StringUtils;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DtoToEntityConversionManager {

	private static final String ERROR_MESSAGE = "Could not read an object of type %s from the request!";
	private static final String NO_CONVERTER_FOUND = "No suitable HttpMessageConverter found to read request body into object of type %s from request with content type of %s!";
	private final DefaultEntityFromDtoCreatorAndMerger entityFromDtoCreator;
	private final List<HttpMessageConverter<?>> messageConverters;
	private final DtoDomainRegistry dtoDomainRegistry;

	private List<DtoHandler> dtoHandlers = new ArrayList<DtoHandler>();

	public DtoToEntityConversionManager(@NonNull DefaultEntityFromDtoCreatorAndMerger entityFromDtoCreator,
			@NonNull List<HttpMessageConverter<?>> messageConverters, @NonNull DtoDomainRegistry dtoDomainRegistry) {
		this.entityFromDtoCreator = entityFromDtoCreator;
		this.messageConverters = messageConverters;
		this.dtoDomainRegistry = dtoDomainRegistry;
	}

	public DtoAndEntityWrapper create(MediaType contentType, HttpInputMessage inputMessage, String dtoParam) {
		DtoInformation dtoInfo = getDtoInformation(dtoParam);
		Object dto = convertToDto(contentType, inputMessage, dtoInfo.getDtoType());

		handle(dto);

		Object entity = entityFromDtoCreator.create(dto, dtoInfo);
		return new DtoAndEntityWrapper(dto, entity);
	}

	public DtoAndEntityWrapper merge(MediaType contentType, HttpInputMessage inputMessage, RepositoryInvoker invoker,
			Serializable id, String dtoParam) {

		DtoInformation dtoInfo = getDtoInformation(dtoParam);
		Object dto = convertToDto(contentType, inputMessage, dtoInfo.getDtoType());

		Object existingObject = invoker.invokeFindOne(id);

		if (existingObject == null) {
			throw new ResourceNotFoundException();
		}

		handle(dto, existingObject);

		entityFromDtoCreator.merge(dto, existingObject, dtoInfo);
		return new DtoAndEntityWrapper(dto, existingObject);
	}

	private void handle(Object dto) {
		for (DtoHandler handler : dtoHandlers)
			handler.handle(dto);
	}

	private void handle(Object dto, Object entity) {
		for (DtoHandler handler : dtoHandlers)
			handler.handle(dto, entity);
	}

	public void addHandler(DtoHandler dtoHandler) {
		dtoHandlers.add(dtoHandler);
	}

	private Object convertToDto(MediaType contentType, HttpInputMessage inputMessage, Class<?> dtoType) {
		for (HttpMessageConverter converter : messageConverters) {

			if (!converter.canRead(PersistentEntityResource.class, contentType)) {
				continue;
			}

			Object dto = read(inputMessage, converter, dtoType);
			if (dto == null) {
				throw new HttpMessageNotReadableException(String.format(ERROR_MESSAGE, dtoType));
			}
			return dto;
		}
		throw new HttpMessageNotReadableException(String.format(NO_CONVERTER_FOUND, dtoType, contentType));
	}

	private DtoInformation getDtoInformation(String dtoParam) {
		if (!StringUtils.hasText(dtoParam))
			throw new OrestException(OrestExceptionDictionary.NO_DTO_PARAMETER, "Request must contain dto parameter");
		DtoInformation dtoInfo = dtoDomainRegistry.get(dtoParam);
		if (dtoInfo == null)
			throw new OrestException(OrestExceptionDictionary.NO_SUCH_DTO, "There is no such dto: " + dtoParam
					+ " defined");
		if (!dtoInfo.isExported())
			throw new OrestException(OrestExceptionDictionary.DTO_NOT_EXPORTED, dtoParam + " is not exported");
		return dtoInfo;
	}

	private Object read(HttpInputMessage inputMessage, HttpMessageConverter<Object> converter, Class<?> type) {
		try {
			return converter.read(type, inputMessage);
		} catch (IOException e) {
			throw new HttpMessageNotReadableException(String.format(ERROR_MESSAGE, type));
		}
	}

	@Data
	public static class DtoAndEntityWrapper {
		private final Object dto;
		private final Object entity;
	}

}
