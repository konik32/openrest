package orest.dto;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class DefaultEntityFromDtoCreator implements EntityFromDtoCreator<Object, Object> {

	private final DtoDomainRegistry registry;
	private final BeanFactory beanFactory;

	final static HashMap<String, Class<? extends Collection>> collectionFallbacks = new HashMap<String, Class<? extends Collection>>();
	static {
		collectionFallbacks.put(Collection.class.getName(), ArrayList.class);
		collectionFallbacks.put(List.class.getName(), ArrayList.class);
		collectionFallbacks.put(Set.class.getName(), HashSet.class);
		collectionFallbacks.put(SortedSet.class.getName(), TreeSet.class);
		collectionFallbacks.put(Queue.class.getName(), LinkedList.class);

		/*
		 * 11-Jan-2009, tatu: Let's see if we can still add support for JDK 1.6
		 * interfaces, even if we run on 1.5. Just need to be more careful with
		 * typos, since compiler won't notice any problems...
		 */
		collectionFallbacks.put("java.util.Deque", LinkedList.class);
		collectionFallbacks.put("java.util.NavigableSet", TreeSet.class);
	}

	public DefaultEntityFromDtoCreator(DtoDomainRegistry registry, BeanFactory beanFactory) {
		Assert.notNull(registry);
		Assert.notNull(beanFactory);
		this.registry = registry;
		this.beanFactory = beanFactory;
	}

	@Override
	public Object create(Object from, DtoInformation dtoInfo) {
		if (!dtoInfo.getEntityCreatorType().equals(void.class)) {
			EntityFromDtoCreator<Object, Object> creator = (EntityFromDtoCreator<Object, Object>) beanFactory.getBean(dtoInfo.getEntityCreatorType());
			return creator.create(from, dtoInfo);
		}
		return createByFields(from, dtoInfo);
	}

	private Object createByFields(final Object from, final DtoInformation dtoInfo) {
		final Object entity = org.springframework.data.util.ReflectionUtils.createInstanceIfPresent(dtoInfo.getEntityType().getName(), null);
		if (entity == null)
			throw new IllegalStateException(dtoInfo.getEntityType() + " does not have default constructor");
		ReflectionUtils.doWithFields(from.getClass(), new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				Field entityField = ReflectionUtils.findField(dtoInfo.getEntityType(), field.getName());
				if (entityField == null)
					return;
				ReflectionUtils.makeAccessible(entityField);
				ReflectionUtils.makeAccessible(field);
				if (Collection.class.isAssignableFrom(field.getType())) {
					Object fieldCollection = field.get(from);
					if(fieldCollection == null) return;
					Iterator<Object> it = ((Collection<Object>) field.get(from)).iterator();
					Collection<Object> entityFieldCollection = null;
					try {
						Class<Collection<Object>> colClass = (Class<Collection<Object>>) collectionFallbacks.get(entityField.getType().getName());
						if (colClass == null)
							throw new IllegalStateException("Cannot set property " + entityField.getName() + " . Collection of type " + entityField.getType()
									+ " is not managed");
						entityFieldCollection = colClass.newInstance();
					} catch (InstantiationException e) {
						throw new IllegalStateException("Cannot set property " + entityField.getName(), e);
					}
					while (it.hasNext()) {
						Object object = it.next();
						DtoInformation subDtoInfo = registry.get(object.getClass());
						Object value = subDtoInfo != null ? create(object, subDtoInfo) : object;
						entityFieldCollection.add(value);
					}
					entityField.set(entity, entityFieldCollection);
				} else {
					DtoInformation subDtoInfo = registry.get(field.getType());
					Object value = subDtoInfo != null ? create(field.get(from), subDtoInfo) : field.get(from);
					entityField.set(entity, value);
				}
			}
		});
		return entity;
	}

}
