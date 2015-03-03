package orest.dto;

import java.util.HashMap;
import java.util.Map;

public class DtoDomainRegistry {

	private Map<Class<?>, DtoInformation> mapping = new HashMap<Class<?>, DtoInformation>();
	private Map<String, DtoInformation> namesMapping = new HashMap<String, DtoInformation>();

	public DtoInformation get(Class<?> dtoType) {
		return mapping.get(dtoType);
	}

	public DtoInformation get(String name) {
		return namesMapping.get(name);
	}
	
	public void put(String name, DtoInformation dtoInfo){
		namesMapping.put(name, dtoInfo);
	}
	
	public void put(Class<?> clazz, DtoInformation dtoInfo){
		mapping.put(clazz, dtoInfo);
	}

//	private void registerDtos(String basePackage) throws ClassNotFoundException {
//		Set<Class<?>> candidates = new AnnotatedTypeScanner(Dto.class).findTypes(basePackage);
//		for (Class<?> dtoClass : candidates) {
//			Dto dtoAnn = AnnotationUtils.findAnnotation(dtoClass, Dto.class);
//			DtoInformation dtoInfo = new DtoInformation(dtoAnn.entityType(), dtoAnn.name(), dtoClass, dtoAnn.entityCreatorType());
//			mapping.put(dtoClass, dtoInfo);
//			if (!dtoInfo.getName().isEmpty())
//				namesMapping.put(dtoInfo.getName(), dtoInfo);
//		}
//	}

}
