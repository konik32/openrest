package orest.dto;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

import orest.dto.Dto.DtoType;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

/**
 * Default implementation of interfaces {@link EntityFromDtoCreator} and
 * {@link EntityFromDtoMerger}. This class creates and merges entities from dto
 * by mapping fields.
 * 
 * @author Szymon Konicki
 *
 */
public class DefaultEntityFromDtoCreator implements EntityFromDtoCreator<Object, Object>,
		EntityFromDtoMerger<Object, Object> {

	private final DtoDomainRegistry registry;
	private final BeanFactory beanFactory;
	private final PersistentEntities persistentEntities;



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

	public DefaultEntityFromDtoCreator(DtoDomainRegistry registry, BeanFactory beanFactory,
			PersistentEntities persistentEntities) {
		Assert.notNull(registry);
		Assert.notNull(beanFactory);
		Assert.notNull(persistentEntities);
		this.registry = registry;
		this.beanFactory = beanFactory;
		this.persistentEntities = persistentEntities;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object create(Object from, DtoInformation dtoInfo) {
		if (dtoInfo.getType() == DtoType.MERGE)
			throw new IllegalStateException("Cannot use merge dto to create entity");
		if (!dtoInfo.getEntityCreatorType().equals(DefaultEntityFromDtoCreator.class)) {
			EntityFromDtoCreator<Object, Object> creator = (EntityFromDtoCreator<Object, Object>) beanFactory
					.getBean(dtoInfo.getEntityCreatorType());
			return creator.create(from, dtoInfo);
		}
		return createByFields(from, dtoInfo);
	}

	private Object createByFields(final Object from, final DtoInformation dtoInfo) {
		final Object entity = org.springframework.data.util.ReflectionUtils.createInstanceIfPresent(dtoInfo
				.getEntityType().getName(), null);
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
					doWithCollectionField(from, field, entityField, entity);
				} else {
					doWithField(from, field, entityField, entity);
				}
			}
		});
		return entity;
	}

	private void doWithField(final Object from, final Field field, final Field entityField, final Object entity)
			throws IllegalArgumentException, IllegalAccessException {
		DtoInformation subDtoInfo = registry.get(field.getType());
		Object value = field.get(from);
		if (value != null && subDtoInfo != null)
			value = create(value, subDtoInfo);
		entityField.set(entity, value);
	}

	@SuppressWarnings("unchecked")
	private void doWithCollectionField(final Object from, final Field field, final Field entityField,
			final Object entity) throws IllegalArgumentException, IllegalAccessException {
		Collection<Object> fieldCollection = ((Collection<Object>) field.get(from));
		if (fieldCollection == null)
			return;
		Collection<Object> entityFieldCollection = createCollection(entityField);
		populateEntityFieldCollection(fieldCollection, entityField, entityFieldCollection);
		entityField.set(entity, entityFieldCollection);
	}

	@SuppressWarnings("unchecked")
	private Collection<Object> createCollection(Field entityField) throws IllegalAccessException {
		Class<Collection<Object>> colClass = (Class<Collection<Object>>) collectionFallbacks.get(entityField.getType()
				.getName());
		if (colClass == null)
			throw new IllegalStateException("Cannot set property " + entityField.getName() + " . Collection of type "
					+ entityField.getType() + " is not managed");
		try {
			return colClass.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalStateException("Cannot create collection of type" + colClass);
		}
	}

	private void populateEntityFieldCollection(Collection<Object> from, Field field,
			Collection<Object> entityFieldCollection) throws IllegalArgumentException, IllegalAccessException {
		Iterator<Object> it = from.iterator();
		while (it.hasNext()) {
			Object object = it.next();
			if (object == null)
				continue;
			DtoInformation subDtoInfo = registry.get(object.getClass());
			Object value = subDtoInfo != null ? create(object, subDtoInfo) : object;
			entityFieldCollection.add(value);
		}
	}

	@Override
	public void merge(Object from, Object entity, DtoInformation dtoInfo) {
		if (dtoInfo.getType() == DtoType.CREATE)
			throw new IllegalStateException("Cannot use create dto to merge with entity");
		if (!dtoInfo.getEntityMergerType().equals(DefaultEntityFromDtoCreator.class)) {
			EntityFromDtoMerger<Object, Object> creator = (EntityFromDtoMerger<Object, Object>) beanFactory
					.getBean(dtoInfo.getEntityMergerType());
			creator.merge(from, entity, dtoInfo);
		}
		mergeByFields(from, entity);
	}

	private void mergeByFields(final Object from, final Object entity) {
		ReflectionUtils.doWithFields(from.getClass(), new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				Field entityField = ReflectionUtils.findField(entity.getClass(), field.getName());
				if (entityField == null)
					return;
				ReflectionUtils.makeAccessible(entityField);
				ReflectionUtils.makeAccessible(field);
				Object value = field.get(from);
				if (!setNull(value, field, from))
					return;
				if (Collection.class.isAssignableFrom(field.getType())) {
					doWithCollectionField(from, field, entityField, entity);
				} else {
					mergeSingleField(entityField, field, entity, value);
				}
			}
		}, new FieldFilter() {
			@Override
			public boolean matches(Field field) {
				int modifiers = field.getModifiers();
				return !(Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers));
			}
		});
	}

	private void mergeSingleField(Field entityField, Field field, Object entity, Object value)
			throws IllegalArgumentException, IllegalAccessException {
		if (persistentEntities.getPersistentEntity(entityField.getType()) == null) {
			setEntityField(entity, entityField, value);
		} else {
			DtoInformation subDtoInfo = registry.get(field.getType());
			if (value == null)
				return;
			if (subDtoInfo == null)
				mergeByFields(value, entity);
			else {
				Object entityFieldValue = entityField.get(entity);
				if (entityFieldValue == null) {
					if (subDtoInfo.getType() == DtoType.MERGE)
						throw new IllegalStateException("Entity of type " + entityField.getType()
								+ " is null. Cannot merge with null object");
					else
						setEntityField(entity, entityField, create(value, subDtoInfo));
				} else {
					merge(value, entityFieldValue, subDtoInfo);
				}
			}

		}
	}

	private void setEntityField(Object entity, Field entityField, Object value) throws IllegalAccessException,
			IllegalArgumentException {
		try {
			Method setter = ReflectionUtils.findMethod(entity.getClass(),
					"set" + WordUtils.capitalize(entityField.getName()), entityField.getType());
			if (setter == null)
				throw new IllegalStateException("There is no setter for field " + entityField.getName() + " in "
						+ entity);
			setter.invoke(entity, value);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	private boolean setNull(Object value, Field field, Object from) throws IllegalArgumentException,
			IllegalAccessException {
		if (value == null) {
			Nullable nullable = field.getAnnotation(Nullable.class);
			if (nullable == null)
				return false;
			Field isSetField = ReflectionUtils.findField(from.getClass(), nullable.value());
			if (isSetField == null)
				throw new IllegalStateException("There is no field " + nullable.value() + " in " + from.getClass());
			ReflectionUtils.makeAccessible(isSetField);
			if (!isSetField.getBoolean(from))
				return false;
		}
		return true;
	}



}
