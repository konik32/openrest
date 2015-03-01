package orest.dto;

import orest.model.dto.ProductDto;
import orest.model.dto.UserDto;

import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

public class DtoDomainRegistryTest {

	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	@Test
	public void testIfRegisterUserAndProductDtos() throws ClassNotFoundException {
		DtoDomainRegistry dtoDomainRegistry = new DtoDomainRegistry(resourceLoader, "orest.model.dto");
		Assert.notNull(dtoDomainRegistry.get("userDto"));
		Assert.notNull(dtoDomainRegistry.get("productDto"));
		Assert.isNull(dtoDomainRegistry.get("tagDto"));
		Assert.notNull(dtoDomainRegistry.get(UserDto.class));
		Assert.notNull(dtoDomainRegistry.get(ProductDto.class));
	}
}
