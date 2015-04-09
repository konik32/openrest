package orest.dto;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import lombok.Data;
import orest.dto.Dto.DtoType;
import orest.expression.SpelEvaluationBeanTest.UserDto;
import orest.model.Product;
import orest.model.Tag;
import orest.model.User;
import orest.model.dto.ProductDto;
import orest.model.dto.TagDto;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.util.Assert;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEntityFromDtoCreatorTest {

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private DtoDomainRegistry dtoDomainRegistry;
	private DefaultEntityFromDtoCreator dtoEntityCreator;
	private ProductDto productDto = new ProductDto();
	private Product product;

	@Before
	public void setUp() {
		when(dtoDomainRegistry.get(any(Class.class))).thenReturn(null);
		when(dtoDomainRegistry.get(orest.model.dto.UserDto.class)).thenReturn(
				new DtoInformation(User.class, "userDto", UserDto.class, DefaultEntityFromDtoCreator.class,
						DefaultEntityFromDtoCreator.class, DtoType.BOTH, null));
		when(dtoDomainRegistry.get(orest.model.dto.TagDto.class)).thenReturn(
				new DtoInformation(Tag.class, "tagDto", TagDto.class, DefaultEntityFromDtoCreator.class,
						DefaultEntityFromDtoCreator.class, DtoType.BOTH, null));

		EntityFromDtoCreator<Object, Object> entityFromDtoCreator = mock(EntityFromDtoCreator.class);
		when(entityFromDtoCreator.create(any(Object.class), any(DtoInformation.class))).thenReturn(
				mock(TestEntity.class));
		when(beanFactory.getBean(EntityFromDtoCreator.class)).thenReturn(entityFromDtoCreator);
		dtoEntityCreator = new DefaultEntityFromDtoCreator(dtoDomainRegistry, beanFactory,
				mock(PersistentEntities.class));

		productDto.setDescription("Lorem Impsum");
		productDto.setName("agd");
		productDto.setTempName("tempName");

		product = new Product();
		product.setDescription("Description");
		product.setName("phone");
		product.setProductionYear(123);

		Tag tag = new Tag();
		tag.setName("old");

		product.setTags(Arrays.asList(tag));
	}

	@Test
	public void testIfCreateCorrectEntity() {
		Product product = (Product) dtoEntityCreator.create(productDto, new DtoInformation(Product.class, "productDto",
				ProductDto.class, DefaultEntityFromDtoCreator.class, DefaultEntityFromDtoCreator.class, DtoType.BOTH,
				null));
		assertEquals("Lorem Impsum", productDto.getDescription());
		productDto.setDescription("Lorem Impsum");
		assertEquals("agd", product.getName());
	}

	@Test(expected = IllegalStateException.class)
	public void testIfThrowsExceptionOnNoDefaultEntityConstructor() {
		dtoEntityCreator.create(productDto, new DtoInformation(TestEntity.class, "productDto", ProductDto.class,
				DefaultEntityFromDtoCreator.class, DefaultEntityFromDtoCreator.class, DtoType.BOTH, null));
	}

	@Test
	public void testIfInvokesSpecifiedEntityFromDtoCreator() {
		TestEntity testEntity = (TestEntity) dtoEntityCreator.create(productDto, new DtoInformation(TestEntity.class,
				"productDto", ProductDto.class, EntityFromDtoCreator.class, DefaultEntityFromDtoCreator.class,
				DtoType.BOTH, null));
		Assert.notNull(testEntity);
	}

	@Test
	public void testIfMapCollectionTypeFields() {
		TagDto tagDto = new TagDto();
		tagDto.setName("name");
		productDto.setTags(Arrays.asList(tagDto));
		Product product = (Product) dtoEntityCreator.create(productDto, new DtoInformation(Product.class, "productDto",
				ProductDto.class, DefaultEntityFromDtoCreator.class, DefaultEntityFromDtoCreator.class, DtoType.BOTH,
				null));
		assertNotNull(product.getTags());
		assertEquals(1, product.getTags().size());
	}

	@Test
	public void testIfMergeEntity() {
		TagDto tagDto = new TagDto();
		tagDto.setName("name");
		productDto.setTags(Arrays.asList(tagDto));
		dtoEntityCreator.merge(productDto, product, new DtoInformation(Product.class, "productDto", ProductDto.class,
				DefaultEntityFromDtoCreator.class, DefaultEntityFromDtoCreator.class, DtoType.BOTH, null));
		assertEquals(productDto.getDescription(), product.getDescription());
		assertEquals(productDto.getName(), product.getName());
		assertEquals(new Integer(123), product.getProductionYear());
		assertEquals(1, product.getTags().size());
		assertEquals(tagDto.getName(), product.getTags().get(0).getName());
	}

	@Test
	public void testIfNullableAnnotatedUserIsSetToNull() {
		product.setUser(new User());
		productDto.setUser(null);
		dtoEntityCreator.merge(productDto, product, new DtoInformation(Product.class, "productDto", ProductDto.class,
				DefaultEntityFromDtoCreator.class, DefaultEntityFromDtoCreator.class, DtoType.BOTH, null));
		assertNull(product.getUser());
	}

	@Test
	public void testIfNullableAnnotatedUserIsNotSetToNull() {
		product.setUser(new User());
		dtoEntityCreator.merge(productDto, product, new DtoInformation(Product.class, "productDto", ProductDto.class,
				DefaultEntityFromDtoCreator.class, DefaultEntityFromDtoCreator.class, DtoType.BOTH, null));
		assertNotNull(product.getUser());
	}

	@Data
	public class TestEntity {
		private final String name;
	}
}
