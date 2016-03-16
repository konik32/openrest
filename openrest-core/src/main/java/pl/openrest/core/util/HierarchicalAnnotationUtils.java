package pl.openrest.core.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;



public final class HierarchicalAnnotationUtils {
	
	private HierarchicalAnnotationUtils(){
		
	}
	
	
	public static <A extends Annotation> List<A> getAllHierarchicalAnnotations(Class<?> clazz, Class<A> annotationType){
		List<A> annotations = new ArrayList<A>();
		
		if(!AnnotationUtils.isAnnotationInherited(annotationType, clazz)){
			A ann = AnnotationUtils.findAnnotation(clazz, annotationType);
			if(ann != null)
				annotations.add(ann);
		}
		if(clazz.getSuperclass() != null)
			annotations.addAll(getAllHierarchicalAnnotations(clazz.getSuperclass(), annotationType));
		return annotations;
	}
}
