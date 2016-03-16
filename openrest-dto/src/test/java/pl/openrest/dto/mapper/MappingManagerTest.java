package pl.openrest.dto.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.dummy.model.User;
import pl.openrest.dto.dummy.model.dto.UserDto;
import pl.openrest.dto.handler.BeforeCreateMappingHandler;
import pl.openrest.dto.handler.BeforeUpdateMappingHandler;
import pl.openrest.dto.mapper.MappingManager.DtoAndEntityWrapper;
import pl.openrest.dto.registry.DtoInformation;
import pl.openrest.dto.registry.DtoInformationRegistry;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class MappingManagerTest {

    @Mock
    private MapperDelegator mapperDelegator;

    @Mock
    private DtoInformationRegistry dtoDomainRegistry;
    @Mock
    private HttpInputMessage inputMessage;

    @Mock
    private RepositoryInvoker invoker;
    @Mock
    private HttpMessageConverter converter;

    private MediaType contentType = MediaType.APPLICATION_JSON;

    private Serializable id = 1l;
    private String dtoParam = "dto";

    private MappingManager mappingManager;

    private DtoInformation dtoInfo;

    @Mock
    private UserDto userDto;

    @Mock
    private User user;

    @Mock
    private BeforeCreateMappingHandler beforeCreateHandler;
    @Mock
    private BeforeUpdateMappingHandler beforeUpdateHandler;

    @Before
    public void setUp() throws Exception {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(converter);

        mappingManager = new MappingManager(mapperDelegator, messageConverters, dtoDomainRegistry);
        mappingManager.addHandler(beforeCreateHandler);
        mappingManager.addHandler(beforeUpdateHandler);
        dtoInfo = new DtoInformation(UserDto.class, UserDto.class.getAnnotation(Dto.class));

        when(dtoDomainRegistry.get(dtoParam)).thenReturn(dtoInfo);
        when(converter.canRead(PersistentEntityResource.class, contentType)).thenReturn(true);
        when(converter.read(UserDto.class, inputMessage)).thenReturn(userDto);
        when(invoker.invokeFindOne(id)).thenReturn(user);
        when(mapperDelegator.create(userDto)).thenReturn(user);
    }

    @Test
    public void shouldCallDtoDomainRegistry() throws Exception {
        // given
        // when
        mappingManager.create(contentType, inputMessage, dtoParam);
        mappingManager.merge(contentType, inputMessage, invoker, id, dtoParam);
        // then
        verify(dtoDomainRegistry, times(2)).get(dtoParam);
    }

    @Test
    public void shouldCallConverterRead() throws Exception {
        mappingManager.create(contentType, inputMessage, dtoParam);
        mappingManager.merge(contentType, inputMessage, invoker, id, dtoParam);
        verify(converter, times(2)).read(UserDto.class, inputMessage);
    }

    @Test
    public void shouldCallEntityFromDtoCreatorCreate() throws Exception {
        mappingManager.create(contentType, inputMessage, dtoParam);
        verify(mapperDelegator, times(1)).create(userDto);
    }

    @Test
    public void shouldCallEntityFromDtoCreatorMerge() throws Exception {
        mappingManager.merge(contentType, inputMessage, invoker, id, dtoParam);
        verify(mapperDelegator, times(1)).merge(userDto, user);
    }

    @Test
    public void shouldCallDtoHandler() throws Exception {
        mappingManager.merge(contentType, inputMessage, invoker, id, dtoParam);
        mappingManager.create(contentType, inputMessage, dtoParam);
        verify(beforeUpdateHandler, times(1)).handle(userDto, user);
        verify(beforeCreateHandler, times(1)).handle(userDto);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionOnInvokeFindOneNull() throws Exception {
        // given
        when(invoker.invokeFindOne(id)).thenReturn(null);
        // when
        mappingManager.merge(contentType, inputMessage, invoker, id, dtoParam);
        // then
    }

    @Test(expected = HttpMessageNotReadableException.class)
    public void shouldThrowHttpMessageNotReadableExceptionOnConverterReadsNull() throws Exception {
        // given
        when(converter.read(UserDto.class, inputMessage)).thenReturn(null);
        // when
        mappingManager.merge(contentType, inputMessage, invoker, id, dtoParam);
        // then
    }

    @Test
    public void shouldReturnDtoAndEntityWrapper() throws Exception {
        // given
        // when
        DtoAndEntityWrapper wrapperMerge = mappingManager.merge(contentType, inputMessage, invoker, id, dtoParam);
        DtoAndEntityWrapper wrapperCreate = mappingManager.create(contentType, inputMessage, dtoParam);
        // then
        assertNotNull(wrapperMerge);
        assertNotNull(wrapperMerge.getDto());
        assertNotNull(wrapperMerge.getEntity());
        assertEquals(userDto, wrapperMerge.getDto());
        assertEquals(user, wrapperMerge.getEntity());

        assertNotNull(wrapperCreate);
        assertNotNull(wrapperCreate.getDto());
        assertNotNull(wrapperCreate.getEntity());
        assertEquals(userDto, wrapperCreate.getDto());
        assertEquals(user, wrapperCreate.getEntity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOrestExceptionIfDtoParameterIsEmpty() throws Exception {
        // when
        mappingManager.merge(contentType, inputMessage, invoker, id, "");
        // then
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOrestExceptionIfNoDtoFound() throws Exception {
        // given
        when(dtoDomainRegistry.get(dtoParam)).thenReturn(null);
        // when
        mappingManager.merge(contentType, inputMessage, invoker, id, dtoParam);
        // then
    }

    @Test(expected = HttpMessageNotReadableException.class)
    public void shouldThrowHttpMessageNotReadableExceptionOnNoConverterFoundForDtoType() throws Exception {
        // given
        when(converter.canRead(PersistentEntityResource.class, contentType)).thenReturn(false);
        // when
        mappingManager.merge(contentType, inputMessage, invoker, id, dtoParam);
        // then
    }

}
