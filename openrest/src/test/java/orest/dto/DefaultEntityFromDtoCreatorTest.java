package orest.dto;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;

import java.util.Arrays;

import lombok.Data;
import orest.expression.SpelEvaluationBeanTest.UserDto;
import orest.model.Product;
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
	
	@Before
	public void setUp(){
		when(dtoDomainRegistry.get(any(Class.class))).thenReturn(null);
		when(dtoDomainRegistry.get(orest.model.dto.UserDto.class)).thenReturn(new DtoInformation(User.class, "userDto", UserDto.class, void.class, void.class));
		
		EntityFromDtoCreator<Object, Object> entityFromDtoCreator = mock(EntityFromDtoCreator.class);
		when(entityFromDtoCreator.create(any(Object.class), any(DtoInformation.class))).thenReturn(mock(TestEntity.class));
		when(beanFactory.getBean(EntityFromDtoCreator.class)).thenReturn(entityFromDtoCreator);
		dtoEntityCreator = new DefaultEntityFromDtoCreator(dtoDomainRegistry, beanFactory, mock(PersistentEntities.class));
		
		productDto.setDescription("Lorem Impsum");
		productDto.setName("agd");
		productDto.setTempName("tempName");
	}
	
	
	@Test
	public void testIfCreateCorrectEntity(){
		Product product = (Product) dtoEntityCreator.create(productDto, new DtoInformation(Product.class, "productDto", ProductDto.class, void.class, void.class));
		assertEquals("Lorem Impsum", productDto.getDescription());
		productDto.setDescription("Lorem Impsum");
		assertEquals("agd", product.getName());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testIfThrowsExceptionOnNoDefaultEntityConstructor(){
		dtoEntityCreator.create(productDto, new DtoInformation(TestEntity.class, "productDto", ProductDto.class, void.class, void.class));
	}
	
	@Test
	public void testIfInvokesSpecifiedEntityFromDtoCreator(){
		TestEntity testEntity = (TestEntity) dtoEntityCreator.create(productDto, new DtoInformation(TestEntity.class, "productDto", ProductDto.class,EntityFromDtoCreator.class, void.class));
		Assert.notNull(testEntity);
	}
	
	@Test
	public void testIfMapCollectionTypeFields(){
		TagDto tagDto = new TagDto();
		tagDto.setName("name");
		productDto.setTags(Arrays.asList(tagDto));
		Product product = (Product) dtoEntityCreator.create(productDto, new DtoInformation(Product.class, "productDto", ProductDto.class, void.class, void.class));
		assertNotNull(product.getTags());
		assertEquals(1, product.getTags().size());
	}
	
	
	@Data
	public class TestEntity{
		private final String name;
	}
}
