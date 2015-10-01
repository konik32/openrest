package pl.openrest.dto.mapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NonNull;

import org.springframework.data.rest.core.invoke.RepositoryInvoker;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;

import pl.openrest.dto.registry.DtoInformation;
import pl.openrest.dto.registry.DtoInformationRegistry;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MappingManager {

    private static final String ERROR_MESSAGE = "Could not read an object of type %s from the request!";
    private static final String NO_CONVERTER_FOUND = "No suitable HttpMessageConverter found to read request body into object of type %s from request with content type of %s!";
    private final List<HttpMessageConverter<?>> messageConverters;
    private final DtoInformationRegistry dtoInfoRegistry;
    private final MapperDelegator mapperDelegator;

    private List<BeforeCreateMappingHandler> beforeCreateHandlers = new ArrayList<BeforeCreateMappingHandler>();
    private List<BeforeUpdateMappingHandler> beforeUpdateHandlers = new ArrayList<BeforeUpdateMappingHandler>();

    public MappingManager(@NonNull MapperDelegator mapperDelegator, @NonNull List<HttpMessageConverter<?>> messageConverters,
            @NonNull DtoInformationRegistry dtoInfoRegistry) {
        this.mapperDelegator = mapperDelegator;
        this.messageConverters = messageConverters;
        this.dtoInfoRegistry = dtoInfoRegistry;
    }

    public DtoAndEntityWrapper create(MediaType contentType, HttpInputMessage inputMessage, String dtoParam) {
        DtoInformation dtoInfo = getDtoInformation(dtoParam);
        Object dto = convertToDto(contentType, inputMessage, dtoInfo.getDtoType());

        handle(dto);

        Object entity = mapperDelegator.create(dto, dtoInfo);
        return new DtoAndEntityWrapper(dto, entity);
    }

    public DtoAndEntityWrapper merge(MediaType contentType, HttpInputMessage inputMessage, RepositoryInvoker invoker, Serializable id,
            String dtoParam) {

        DtoInformation dtoInfo = getDtoInformation(dtoParam);
        Object dto = convertToDto(contentType, inputMessage, dtoInfo.getDtoType());

        Object existingObject = invoker.invokeFindOne(id);

        if (existingObject == null) {
            throw new ResourceNotFoundException();
        }

        handle(dto, existingObject);

        mapperDelegator.merge(dto, existingObject, dtoInfo);
        return new DtoAndEntityWrapper(dto, existingObject);
    }

    private void handle(Object dto) {
        for (BeforeCreateMappingHandler handler : beforeCreateHandlers)
            handler.handle(dto);
    }

    private void handle(Object dto, Object entity) {
        for (BeforeUpdateMappingHandler handler : beforeUpdateHandlers)
            handler.handle(dto, entity);
    }

    public void addHandler(BeforeCreateMappingHandler handler) {
        beforeCreateHandlers.add(handler);
    }

    public void addHandler(BeforeUpdateMappingHandler handler) {
        beforeUpdateHandlers.add(handler);
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
            throw new IllegalArgumentException("Request must contain dto parameter");
        DtoInformation dtoInfo = dtoInfoRegistry.get(dtoParam);
        if (dtoInfo == null)
            throw new IllegalArgumentException(String.format("There is no such dto: %s defined or it is not exported", dtoParam));
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
