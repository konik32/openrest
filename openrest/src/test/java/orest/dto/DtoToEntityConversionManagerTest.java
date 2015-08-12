package orest.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import orest.dto.Dto.DtoType;
import orest.dto.DtoToEntityConversionManager.DtoAndEntityWrapper;
import orest.dto.handler.DtoHandler;
import orest.exception.OrestException;
import orest.model.User;
import orest.model.dto.UserDto;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.rest.core.invoke.RepositoryInvoker;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class DtoToEntityConversionManagerTest {

	@Mock
	private DefaultEntityFromDtoCreatorAndMerger entityFromDtoCreator;

	@Mock
	private DtoDomainRegistry dtoDomainRegistry;
	@Mock
	private HttpInputMessage inputMessage;

	@Mock
	private RepositoryInvoker invoker;
	@Mock
	private HttpMessageConverter converter;

	private MediaType contentType = MediaType.APPLICATION_JSON;

	private Serializable id = 1l;
	private String dtoParam = "dto";

	private DtoToEntityConversionManager conversionManager;

	private DtoInformation dtoInfo;

	@Mock
	private UserDto userDto;
	
	@Mock
	private User user;
	
	@Mock
	private DtoHandler dtoHandler;

	@Before
	public void setUp() throws Exception {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(converter);

		conversionManager = new DtoToEntityConversionManager(entityFromDtoCreator, messageConverters, dtoDomainRegistry);
		conversionManager.addHandler(dtoHandler);

		dtoInfo = new DtoInformation(User.class, dtoParam, UserDto.class, EntityFromDtoCreator.class,
				EntityFromDtoMerger.class, DtoType.BOTH, true);

		when(dtoDomainRegistry.get(dtoParam)).thenReturn(dtoInfo);
		when(converter.canRead(PersistentEntityResource.class, contentType)).thenReturn(true);
		when(converter.read(UserDto.class, inputMessage)).thenReturn(userDto);
		when(invoker.invokeFindOne(id)).thenReturn(user);
		when(entityFromDtoCreator.create(userDto, dtoInfo)).thenReturn(user);
	}

	@Test
	public void shouldCallDtoDomainRegistry() throws Exception {
		// given
		// when
		conversionManager.create(contentType, inputMessage, dtoParam);
		conversionManager.merge(contentType, inputMessage, invoker, id, dtoParam);
		// then
		verify(dtoDomainRegistry, times(2)).get(dtoParam);
	}

	@Test
	public void shouldCallConverterRead() throws Exception {
		conversionManager.create(contentType, inputMessage, dtoParam);
		conversionManager.merge(contentType, inputMessage, invoker, id, dtoParam);
		verify(converter, times(2)).read(UserDto.class, inputMessage);
	}
	
	@Test
	public void shouldCallEntityFromDtoCreatorCreate() throws Exception {
		conversionManager.create(contentType, inputMessage, dtoParam);
		verify(entityFromDtoCreator, times(1)).create(userDto, dtoInfo);
	}
	
	@Test
	public void shouldCallEntityFromDtoCreatorMerge() throws Exception {
		conversionManager.merge(contentType, inputMessage, invoker, id, dtoParam);
		verify(entityFromDtoCreator, times(1)).merge(userDto, user,dtoInfo);
	}
	
	@Test
	public void shouldCallDtoHandler() throws Exception {
		conversionManager.merge(contentType, inputMessage, invoker, id, dtoParam);
		conversionManager.create(contentType, inputMessage, dtoParam);
		verify(dtoHandler, times(1)).handle(userDto, user);
		verify(dtoHandler, times(1)).handle(userDto);
	}
	
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionOnInvokeFindOneNull() throws Exception {
		// given
		when(invoker.invokeFindOne(id)).thenReturn(null);
		// when
		conversionManager.merge(contentType, inputMessage, invoker, id, dtoParam);
		// then
	}
	
	@Test(expected=HttpMessageNotReadableException.class)
	public void shouldThrowHttpMessageNotReadableExceptionOnConverterReadsNull() throws Exception {
		// given
		when(converter.read(UserDto.class, inputMessage)).thenReturn(null);
		// when
		conversionManager.merge(contentType, inputMessage, invoker, id, dtoParam);
		// then
	}
	
	
	@Test
	public void shouldReturnDtoAndEntityWrapper() throws Exception {
		// given
		// when
		DtoAndEntityWrapper wrapperMerge = conversionManager.merge(contentType, inputMessage, invoker, id, dtoParam);
		DtoAndEntityWrapper wrapperCreate = conversionManager.create(contentType, inputMessage, dtoParam);
		// then
		assertNotNull(wrapperMerge);
		assertNotNull(wrapperMerge.getDto());
		assertNotNull(wrapperMerge.getEntity());
		assertEquals(userDto,wrapperMerge.getDto());
		assertEquals(user, wrapperMerge.getEntity());
		
		assertNotNull(wrapperCreate);
		assertNotNull(wrapperCreate.getDto());
		assertNotNull(wrapperCreate.getEntity());
		assertEquals(userDto,wrapperCreate.getDto());
		assertEquals(user, wrapperCreate.getEntity());
	}
	
	@Test(expected=OrestException.class)
	public void shouldThrowOrestExceptionIfDtoParameterIsEmpty() throws Exception {
		// when
		conversionManager.merge(contentType, inputMessage, invoker, id, "");
		// then
	}
	
	@Test(expected=OrestException.class)
	public void shouldThrowOrestExceptionIfNoDtoFound() throws Exception {
		//given
		when(dtoDomainRegistry.get(dtoParam)).thenReturn(null);
		// when
		conversionManager.merge(contentType, inputMessage, invoker, id, dtoParam);
		// then
	}
	
	@Test(expected=HttpMessageNotReadableException.class)
	public void shouldThrowHttpMessageNotReadableExceptionOnNoConverterFoundForDtoType() throws Exception {
		//given
		when(converter.canRead(PersistentEntityResource.class, contentType)).thenReturn(false);
		// when
		conversionManager.merge(contentType, inputMessage, invoker, id, dtoParam);
		// then
	}
	@Test(expected=OrestException.class)
	public void shouldThrowOrestExceptionOnDtoNotExported(){
		// given
		DtoInformation dtoInfo = mock(DtoInformation.class);
		when(dtoInfo.isExported()).thenReturn(false);
		when(dtoDomainRegistry.get(dtoParam)).thenReturn(dtoInfo);
		//when
		conversionManager.create(contentType, inputMessage, dtoParam);
	}
	
	
}
