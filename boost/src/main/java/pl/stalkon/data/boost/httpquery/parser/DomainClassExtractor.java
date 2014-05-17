package pl.stalkon.data.boost.httpquery.parser;

import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;

import org.springframework.util.StringUtils;

public class DomainClassExtractor {

	
	@PersistenceContext
	private EntityManager entityManager;
	
	public Class<?> getDomainClass(String name){
		Iterator<EntityType<?>> it = entityManager.getMetamodel().getEntities().iterator();
		while(it.hasNext()){
			EntityType<?> entity = it.next();
			if(entity.getName().equals(StringUtils.capitalize(name)))
				return entity.getJavaType();
		}
		return null;
	}
}
