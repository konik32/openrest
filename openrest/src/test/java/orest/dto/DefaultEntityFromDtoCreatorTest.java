package orest.dto;

import static org.mockito.Mockito.when;

import java.util.Arrays;

import orest.dto.Dto.DtoType;
import orest.model.Product;
import orest.model.Tag;
import orest.model.User;
import orest.model.dto.ProductDto;
import orest.model.dto.TagDto;
import orest.model.dto.UserDto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.PersistentEntities;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEntityFromDtoCreatorTest {

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private DtoDomainRegistry dtoDomainRegistry;

	@Mock
	private PersistentEntities persistentEntities;
	@Mock
	private EntityFromDtoCreator<Object, Object> entityFromDtoCreator;
	@Mock
	private DtoInformation productDtoInfo;
	@Mock
	private DtoInformation tagDtoInfo;
	@Mock
	private DtoInformation userDtoInfo;
	@InjectMocks
	private DefaultEntityFromDtoCreatorAndMerger dtoEntityCreator;

	private ProductDto productDto = new ProductDto();
	private Product product = new Product();
	private TagDto tagDto = new TagDto();
	private UserDto userDto = new UserDto();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(productDtoInfo.getType()).thenReturn(DtoType.CREATE);
		Mockito.doReturn(Product.class).when(productDtoInfo).getEntityType();
		Mockito.doReturn(DefaultEntityFromDtoCreatorAndMerger.class).when(productDtoInfo).getEntityCreatorType();
		Mockito.doReturn(DefaultEntityFromDtoCreatorAndMerger.class).when(productDtoInfo).getEntityMergerType();

		when(tagDtoInfo.getType()).thenReturn(DtoType.CREATE);
		Mockito.doReturn(Tag.class).when(tagDtoInfo).getEntityType();
		Mockito.doReturn(DefaultEntityFromDtoCreatorAndMerger.class).when(tagDtoInfo).getEntityCreatorType();

		when(userDtoInfo.getType()).thenReturn(DtoType.CREATE);
		Mockito.doReturn(User.class).when(userDtoInfo).getEntityType();
		Mockito.doReturn(DefaultEntityFromDtoCreatorAndMerger.class).when(userDtoInfo).getEntityCreatorType();

		when(dtoDomainRegistry.get(ProductDto.class)).thenReturn(productDtoInfo);
		when(dtoDomainRegistry.get(TagDto.class)).thenReturn(tagDtoInfo);
		when(dtoDomainRegistry.get(UserDto.class)).thenReturn(userDtoInfo);

		when(beanFactory.getBean(EntityFromDtoCreator.class)).thenReturn(entityFromDtoCreator);

		when(persistentEntities.getPersistentEntity(Tag.class)).thenReturn(Mockito.mock(PersistentEntity.class));
		when(persistentEntities.getPersistentEntity(User.class)).thenReturn(Mockito.mock(PersistentEntity.class));
		when(persistentEntities.getPersistentEntity(Product.class)).thenReturn(Mockito.mock(PersistentEntity.class));

		dtoEntityCreator = new DefaultEntityFromDtoCreatorAndMerger(dtoDomainRegistry, beanFactory, persistentEntities);

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
	public void shouldCallCustomEntityCreator() {
		// given
		Mockito.doReturn(EntityFromDtoCreator.class).when(productDtoInfo).getEntityCreatorType();
		// call
		dtoEntityCreator.create(new Object(), productDtoInfo);
		// verify
		Mockito.verify(entityFromDtoCreator, Mockito.times(1))
				.create(Matchers.anyObject(), Matchers.eq(productDtoInfo));

	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowIllegalStateExceptionOnDtoTypeMergeWhileCreating() {
		// call
		when(productDtoInfo.getType()).thenReturn(DtoType.MERGE);
		dtoEntityCreator.create(new Object(), productDtoInfo);
	}

	@Test
	public void shouldCreateCollectionOfEntitiesFromCollectionOfDtos() {
		// given

		productDto.setTags(Arrays.asList(tagDto, tagDto, tagDto));
		// call
		Product product = (Product) dtoEntityCreator.create(productDto, productDtoInfo);
		// verify
		Assert.assertEquals(Tag.class, product.getTags().get(0).getClass());
		Assert.assertEquals(3, product.getTags().size());
	}

	@Test
	public void shouldCreateCorrectEntity() {

		// call
		Product product = (Product) dtoEntityCreator.create(productDto, productDtoInfo);

		// verify
		Assert.assertEquals("Lorem Impsum", product.getDescription());
		Assert.assertEquals("agd", product.getName());
		Assert.assertEquals("tag", product.getTags().get(0).getName());
	}

	@Test
	public void shouldMergeEntity() {
		// given

		when(productDtoInfo.getType()).thenReturn(DtoType.MERGE);
		//call
		dtoEntityCreator.merge(productDto, product, productDtoInfo);
		
		//verify
		Assert.assertEquals(productDto.getDescription(), product.getDescription());
		Assert.assertEquals(productDto.getName(), product.getName());
		Assert.assertEquals(new Integer(123), product.getProductionYear());
		Assert.assertEquals(1, product.getTags().size());
		Assert.assertEquals(Tag.class, product.getTags().get(0).getClass());
		Assert.assertEquals(tagDto.getName(), product.getTags().get(0).getName());
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowIllegalStateExceptionOnDtoTypeCreateWhileMerging() {
		// given
		when(productDtoInfo.getType()).thenReturn(DtoType.CREATE);
		// call
		dtoEntityCreator.merge(productDto, product, productDtoInfo);
	}

	@Test
	public void shouldNotThrowExceptionOnDtoTypeBoth() {
		// given
		when(productDtoInfo.getType()).thenReturn(DtoType.BOTH);
		// call
		dtoEntityCreator.create(productDto, productDtoInfo);
		dtoEntityCreator.merge(productDto, product, productDtoInfo);
	}

	//
	@Test
	public void shouldSetNullableAnnotatedFieldToNull() {
		// given
		product.setUser(new User());
		productDto.setUser(null);
		when(productDtoInfo.getType()).thenReturn(DtoType.MERGE);
		// call
		dtoEntityCreator.merge(productDto, product, productDtoInfo);

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
		dtoEntityCreator.merge(productDto, product, productDtoInfo);
		// verify
		Assert.assertNotNull(product.getUser());
	}

	@Test
	public void shouldCreateEntityWhileMergingToNullEntityFieldWhenSubDtoTypeIsNotMerge() {
		// given
		when(productDtoInfo.getType()).thenReturn(DtoType.MERGE);
		// call
		dtoEntityCreator.merge(productDto, product, productDtoInfo);
		// verify
		Assert.assertNotNull(product.getUser());
		Assert.assertEquals(User.class, product.getUser().getClass());
		Assert.assertEquals(userDto.getName(), product.getUser().getName());
	}
	
	
	
	
}
