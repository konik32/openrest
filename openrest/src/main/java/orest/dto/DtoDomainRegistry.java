package orest.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.util.AnnotatedTypeScanner;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class DtoDomainRegistry {

	private Map<Class<?>, DtoInformation> mapping = new HashMap<Class<?>, DtoInformation>();
	private Map<String, DtoInformation> namesMapping = new HashMap<String, DtoInformation>();

	public DtoDomainRegistry(ResourceLoader resourceLoader, String basePackage) throws ClassNotFoundException {
		Assert.notNull(resourceLoader);

		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.setResourceLoader(resourceLoader);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Dto.class));
		registerDtos(scanner, basePackage);
	}

	public DtoInformation get(Class<?> dtoType) {
		return mapping.get(dtoType);
	}

	public DtoInformation get(String name) {
		return namesMapping.get(name);
	}

	private void registerDtos(ClassPathScanningCandidateComponentProvider scanner, String basePackage) throws ClassNotFoundException {
		Set<Class<?>> candidates = new AnnotatedTypeScanner(Dto.class).findTypes(basePackage);
		for (Class<?> dtoClass : candidates) {
//		for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
//			Class<?> dtoClass = ClassUtils.forName(candidate.getBeanClassName(), DtoDomainRegistry.class.getClassLoader());
			Dto dtoAnn = AnnotationUtils.findAnnotation(dtoClass, Dto.class);
			DtoInformation dtoInfo = new DtoInformation(dtoAnn.entityType(), dtoAnn.name(), dtoClass, dtoAnn.entityCreatorType());
			mapping.put(dtoClass, dtoInfo);
			if (!dtoInfo.getName().isEmpty())
				namesMapping.put(dtoInfo.getName(), dtoInfo);
		}
	}

}
