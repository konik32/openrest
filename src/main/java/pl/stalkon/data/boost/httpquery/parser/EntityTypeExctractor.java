package pl.stalkon.data.boost.httpquery.parser;

import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class EntityTypeExctractor {
	

	@PersistenceContext
	private EntityManager entityManager;

	public Class<?> getEntityType(String name) {
		Iterator<EntityType<?>> it = entityManager.getMetamodel().getEntities()
				.iterator();
		while (it.hasNext()) {
			EntityType<?> entity = it.next();
			if (entity.getName().equals(StringUtils.capitalize(name)))
				return entity.getJavaType();
		}
		return null;
	}

	public boolean isEntity(String name) {
		return getEntityType(name) != null;
	}
}
