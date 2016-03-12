package pl.openrest.dto.mapper;

import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.util.ReflectionUtils;

import pl.openrest.dto.dummy.model.Product;
import pl.openrest.dto.dummy.model.Tag;
import pl.openrest.dto.dummy.model.User;
import pl.openrest.dto.dummy.model.dto.ProductDto;
import pl.openrest.dto.dummy.model.dto.ProductMergeDto;
import pl.openrest.dto.dummy.model.dto.TagDto;
import pl.openrest.dto.dummy.model.dto.UserDto;
import pl.openrest.dto.registry.DtoInformation;
import pl.openrest.dto.registry.DtoInformationRegistry;
import pl.openrest.dto.registry.DtoType;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCreateAndUpdateMapperTest {

	@Mock
	private DtoInformationRegistry dtoInfoRegistry;

	@Mock
	private CreateMapper<Object, Object> createMapper;

	@Mock
	private PersistentEntities persistentEntities;

	@Mock
	private MapperDelegator mapperDelegator;

	@Mock
	private DtoInformation productDtoInfo;
	@Mock
	private DtoInformation tagDtoInfo;
	@Mock
	private DtoInformation userDtoInfo;

	private DefaultCreateAndUpdateMapper defaultMapper;

	private ProductDto productDto = new ProductDto();
	private Product product = new Product();
	private TagDto tagDto = new TagDto();
	private UserDto userDto = new UserDto();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		MockitoAnnotations.initMocks(this);
		when(productDtoInfo.getType()).thenReturn(DtoType.CREATE);
		Mockito.doReturn(Product.class).when(productDtoInfo).getEntityType();

		when(tagDtoInfo.getType()).thenReturn(DtoType.CREATE);
		Mockito.doReturn(Tag.class).when(tagDtoInfo).getEntityType();

		when(userDtoInfo.getType()).thenReturn(DtoType.CREATE);
		Mockito.doReturn(User.class).when(userDtoInfo).getEntityType();

		when(dtoInfoRegistry.get(ProductDto.class)).thenReturn(productDtoInfo);
		when(dtoInfoRegistry.get(TagDto.class)).thenReturn(tagDtoInfo);
		when(dtoInfoRegistry.get(UserDto.class)).thenReturn(userDtoInfo);

		when(dtoInfoRegistry.contains(ProductDto.class)).thenReturn(true);
		when(dtoInfoRegistry.contains(TagDto.class)).thenReturn(true);
		when(dtoInfoRegistry.contains(UserDto.class)).thenReturn(true);

		when(persistentEntities.getPersistentEntity(Tag.class)).thenReturn(
				Mockito.mock(PersistentEntity.class));
		when(persistentEntities.getPersistentEntity(User.class)).thenReturn(
				Mockito.mock(PersistentEntity.class));
		when(persistentEntities.getPersistentEntity(Product.class)).thenReturn(
				Mockito.mock(PersistentEntity.class));

		defaultMapper = new DefaultCreateAndUpdateMapper(dtoInfoRegistry,
				mapperDelegator, persistentEntities);

		when(mapperDelegator.create(Mockito.any())).then(
				AdditionalAnswers.delegatesTo(defaultMapper));

		productDto.setDescription("Lorem Impsum");
		productDto.setName("agd");
		productDto.setTempName("tempName");

		userDto.setName("agd");
		tagDto.setName("tag");

		productDto.setUser(userDto);
		productDto.setTags(Arrays.asList(tagDto));

		product.setDescription("Description");
		product.setName("phone");
		product.setProductionYear(123);
	}

	@Test
	public void shouldCallMapperDelegatorWhenSubDtoInfoExists() {
		// given
		Mockito.doReturn(Tag.class).when(tagDtoInfo).getEntityType();
		// call
		defaultMapper.create(productDto);
		// verify
		Mockito.verify(mapperDelegator, Mockito.times(2)).create(Mockito.any());

	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowIllegalStateExceptionOnDtoTypeMergeWhileCreating() {
		// call
		when(productDtoInfo.getType()).thenReturn(DtoType.MERGE);
		defaultMapper.create(new ProductDto());
	}

	@Test
	public void shouldCreateCollectionOfEntitiesFromCollectionOfDtos() {
		// given

		productDto.setTags(Arrays.asList(tagDto, tagDto, tagDto));
		// call
		Product product = (Product) defaultMapper.create(productDto);
		// verify
		Assert.assertEquals(Tag.class, product.getTags().get(0).getClass());
		Assert.assertEquals(3, product.getTags().size());
	}

	@Test
	public void shouldCreateCorrectEntity() {

		// call
		Product product = (Product) defaultMapper.create(productDto);

		// verify
		Assert.assertEquals("Lorem Impsum", product.getDescription());
		Assert.assertEquals("agd", product.getName());
		Assert.assertEquals("tag", product.getTags().get(0).getName());
	}

	@Test
	public void shouldMergeEntity() {
		// given

		when(productDtoInfo.getType()).thenReturn(DtoType.MERGE);
		// call
		defaultMapper.merge(productDto, product);

		// verify
		Assert.assertEquals(productDto.getDescription(),
				product.getDescription());
		Assert.assertEquals(productDto.getName(), product.getName());
		Assert.assertEquals(new Integer(123), product.getProductionYear());
		Assert.assertEquals(1, product.getTags().size());
		Assert.assertEquals(Tag.class, product.getTags().get(0).getClass());
		Assert.assertEquals(tagDto.getName(), product.getTags().get(0)
				.getName());
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowIllegalStateExceptionOnDtoTypeCreateWhileMerging() {
		// given
		when(productDtoInfo.getType()).thenReturn(DtoType.CREATE);
		// call
		defaultMapper.merge(productDto, product);
	}

	@Test
	public void shouldNotThrowExceptionOnDtoTypeBoth() {
		// given
		when(productDtoInfo.getType()).thenReturn(DtoType.BOTH);
		// call
		defaultMapper.create(productDto);
		defaultMapper.merge(productDto, product);
	}

	//
	@Test
	public void shouldSetNullableAnnotatedFieldToNull() {
		// given
		product.setUser(new User());
		productDto.setUser(null);
		when(productDtoInfo.getType()).thenReturn(DtoType.MERGE);
		// call
		defaultMapper.merge(productDto, product);

		// verify
		Assert.assertNull(product.getUser());
	}

	@Test
	public void shouldNotSetNullableAnnotatedFieldIfNotSetToNull() {
		// given
		product.setUser(new User());
		productDto.setUser(null);
		productDto.setUserSet(false);
		when(productDtoInfo.getType()).thenReturn(DtoType.MERGE);
		// call
		defaultMapper.merge(productDto, product);
		// verify
		Assert.assertNotNull(product.getUser());
	}

	@Test
	public void shouldCreateEntityWhileMergingToNullEntityFieldWhenSubDtoTypeIsNotMerge() {
		// given
		when(productDtoInfo.getType()).thenReturn(DtoType.MERGE);
		// call
		defaultMapper.merge(productDto, product);
		// verify
		Assert.assertNotNull(product.getUser());
		Assert.assertEquals(User.class, product.getUser().getClass());
		Assert.assertEquals(userDto.getName(), product.getUser().getName());
	}

	/**
	 * Default mapper fails on updating entity when dto contains entity
	 * reference #16
	 */
	@Test
	public void shouldMergeNestedEntityWithDtoNestedEntityByUpdatingId() throws Exception {
		// given

		ProductMergeDto productMergeDto = new ProductMergeDto();
		User userInDto = new User();
		userInDto.setId(2l);
		userInDto.setName("John");
		productMergeDto.setUser(userInDto);
		User user = new User();
		user.setId(1l);
		product.setUser(user);
		product.setName("AGD");
		
		when(dtoInfoRegistry.contains(ProductMergeDto.class)).thenReturn(true);
		when(dtoInfoRegistry.get(ProductMergeDto.class)).thenReturn(productDtoInfo);
		when(productDtoInfo.getType()).thenReturn(DtoType.MERGE);
		
		PersistentEntity userPE = Mockito.mock(PersistentEntity.class);
		PersistentProperty idProperty = Mockito.mock(PersistentProperty.class);
		Mockito.when(idProperty.getField()).thenReturn(ReflectionUtils.findField(User.class, "id"));
		
		Mockito.when(userPE.getIdProperty()).thenReturn(idProperty);
		when(persistentEntities.getPersistentEntity(User.class)).thenReturn(userPE);
		// call
		defaultMapper.merge(productMergeDto, product);
		// verify
		Assert.assertEquals("AGD", product.getName());
	}
}
