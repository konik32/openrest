package orest.dto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import orest.model.dto.ProductDto;
import orest.model.dto.UserDto;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.util.AnnotatedTypeScanner;
import org.springframework.util.Assert;

public class DtoDomainRegistryTest {

	private DtoDomainRegistry registry;

	@Before
	public void setUp() {
		Set<String> packagesToScan = new HashSet<String>(Arrays.asList("orest"));
		registry = new DtoDomainRegistry();
		Set<Class<?>> candidates = new AnnotatedTypeScanner(Dto.class).findTypes(packagesToScan);
		for (Class<?> dtoClass : candidates) {
			Dto dtoAnn = AnnotationUtils.findAnnotation(dtoClass, Dto.class);
			DtoInformation dtoInfo = new DtoInformation(dtoAnn.entityType(), dtoAnn.name(), dtoClass, dtoAnn.entityCreatorType(), dtoAnn.entityMergerType());
			registry.put(dtoClass, dtoInfo);
			if (!dtoInfo.getName().isEmpty())
				registry.put(dtoInfo.getName(), dtoInfo);
		}
	}

	@Test
	public void testIfRegisterUserAndProductDtos() throws ClassNotFoundException {
		Assert.notNull(registry.get("userDto"));
		Assert.notNull(registry.get("productDto"));
		Assert.isNull(registry.get("tagDto"));
		Assert.notNull(registry.get(UserDto.class));
		Assert.notNull(registry.get(ProductDto.class));
	}
}
