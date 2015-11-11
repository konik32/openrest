package pl.openrest.dto.mapper;

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
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.core.CollectionFactory;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import pl.openrest.dto.annotations.Nullable;
import pl.openrest.dto.registry.DtoInformation;
import pl.openrest.dto.registry.DtoInformationRegistry;
import pl.openrest.dto.registry.DtoType;

/**
 * Default implementation of interfaces {@link EntityFromDtoCreator} and {@link EntityFromDtoMerger}. This class creates and merges entities
 * from dto by mapping fields.
 * 
 * @author Szymon Konicki
 *
 */
public class DefaultCreateAndUpdateMapper implements CreateMapper<Object, Object>, UpdateMapper<Object, Object> {

    private final DtoInformationRegistry dtoInfoRegistry;
    private final MapperDelegator mapperDelegator;
    private final PersistentEntities persistentEntities;

    public DefaultCreateAndUpdateMapper(DtoInformationRegistry dtoInfoRegistry, MapperDelegator mapperDelegator,
            PersistentEntities persistentEntities) {
        Assert.notNull(dtoInfoRegistry);
        Assert.notNull(mapperDelegator);
        Assert.notNull(persistentEntities);
        this.dtoInfoRegistry = dtoInfoRegistry;
        this.mapperDelegator = mapperDelegator;
        this.persistentEntities = persistentEntities;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object create(Object from) {
        DtoInformation dtoInfo = dtoInfoRegistry.get(from.getClass());
        if (dtoInfo.getType() == DtoType.MERGE)
            throw new IllegalStateException("Cannot use merge dto to create entity");
        return createByFields(from, dtoInfo);
    }

    /**
     * Method takes each dto field, checks whether entity has with with same name and maps it.
     * 
     * @param from
     *            - dto object
     * @param dtoInfo
     * @return
     */
    private Object createByFields(final Object from, final DtoInformation dtoInfo) {
        final Object entity = org.springframework.data.util.ReflectionUtils
                .createInstanceIfPresent(dtoInfo.getEntityType().getName(), null);
        if (entity == null)
            throw new IllegalStateException(dtoInfo.getEntityType() + " does not have default constructor");
        ReflectionUtils.doWithFields(from.getClass(), new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                Field entityField = ReflectionUtils.findField(dtoInfo.getEntityType(), field.getName());
                if (entityField == null)
                    return;
                if (isFinalOrStatic(entityField))
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

    /**
     * Method handles fields that are not collections. It checks whether dto's field is dto itself. If so it invokes create method,
     * otherwise entity field is set to dto's field value.
     * 
     * @param from
     *            - dto
     * @param field
     *            -dto's field
     * @param entityField
     * @param entity
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void doWithField(final Object from, final Field field, final Field entityField, final Object entity)
            throws IllegalArgumentException, IllegalAccessException {
        Object value = field.get(from);
        if (value != null && dtoInfoRegistry.contains(field.getType()))
            value = mapperDelegator.create(value);
        entityField.set(entity, value);
    }

    /**
     * Method handles collection fields.
     * 
     * @param from
     *            - dto
     * @param field
     *            - dto's field
     * @param entityField
     * @param entity
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    private void doWithCollectionField(final Object from, final Field field, final Field entityField, final Object entity)
            throws IllegalArgumentException, IllegalAccessException {
        Collection<Object> fieldCollection = (Collection<Object>) field.get(from);
        Collection<Object> entityFieldCollection = CollectionFactory.createCollection(entityField.getType(), 0);
        populateEntityFieldCollection(fieldCollection, entityFieldCollection);
        entityField.set(entity, entityFieldCollection);
    }

    /**
     * Method populates entity field collection with dto's field collection objects. If any of dto's field collection objects is dto itselt
     * method invokes create method.
     * 
     * @param from
     *            - dto
     * @param field
     *            - dto's field
     * @param entityFieldCollection
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void populateEntityFieldCollection(Collection<Object> from, Collection<Object> entityFieldCollection)
            throws IllegalArgumentException, IllegalAccessException {
        Iterator<Object> it = from.iterator();
        while (it.hasNext()) {
            Object object = it.next();
            if (object == null)
                continue;
            Object value = dtoInfoRegistry.contains(object.getClass())? mapperDelegator.create(object) : object;
            entityFieldCollection.add(value);
        }
    }

    /**
     * Method checks whether passed dto has custom {@link EntityFromDtoMerger}. If so it invokes that creator, otherwise entity is merged
     * with dto by invoking setter methods. For null dto's fields, method checks whether it is supposed to set entity with null value or not
     * ({@link Nullable}). Collections are not merged, always new collection is set for entity field.
     * 
     * @param from
     *            - dto
     */
    @Override
    public void merge(Object from, Object entity) {
        DtoInformation dtoInfo = dtoInfoRegistry.get(from.getClass());
        if (dtoInfo.getType() == DtoType.CREATE)
            throw new IllegalStateException("Cannot use create dto to merge with entity");
        mergeByFields(from, entity);
    }

    private void mergeByFields(final Object from, final Object entity) {
        ReflectionUtils.doWithFields(from.getClass(), new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                Field entityField = ReflectionUtils.findField(entity.getClass(), field.getName());
                if (entityField == null)
                    return;
                if (isFinalOrStatic(entityField))
                    return;
                ReflectionUtils.makeAccessible(entityField);
                ReflectionUtils.makeAccessible(field);
                Object value = field.get(from);
                if (setNull(value, field, from)) {
                    setEntityField(entity, entityField, null);
                    return;
                } else if (value == null)
                    return;
                if (Collection.class.isAssignableFrom(field.getType())) {
                    doWithCollectionField(from, field, entityField, entity);
                } else {
                    mergeSingleField(entityField, field, entity, value);
                }
            }
        });
    }

    private void mergeSingleField(Field entityField, Field field, Object entity, Object value) throws IllegalArgumentException,
            IllegalAccessException {
        if (persistentEntities.getPersistentEntity(entityField.getType()) == null) {
            setEntityField(entity, entityField, value);
        } else {
            DtoInformation subDtoInfo = dtoInfoRegistry.get(field.getType());
            if (value == null)
                return;
            if (subDtoInfo == null)
                mergeByFields(value, entity);
            else {
                Object entityFieldValue = entityField.get(entity);
                if (entityFieldValue == null) {
                    if (subDtoInfo.getType() == DtoType.MERGE)
                        throw new IllegalStateException(
                                "Entity of type "
                                        + entityField.getType()
                                        + " is null. Cannot merge with null object. If you want to create new entity use dto of type CREATE or BOTH.");
                    else
                        setEntityField(entity, entityField, mapperDelegator.create(value));
                } else {
                    mapperDelegator.merge(value, entityFieldValue);
                }
            }

        }
    }

    private void setEntityField(Object entity, Field entityField, Object value) throws IllegalAccessException, IllegalArgumentException {
        try {
            Method setter = ReflectionUtils.findMethod(entity.getClass(), "set" + WordUtils.capitalize(entityField.getName()),
                    entityField.getType());
            if (setter == null)
                throw new IllegalStateException("There is no setter for field " + entityField.getName() + " in " + entity);
            setter.invoke(entity, value);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean setNull(Object value, Field field, Object from) throws IllegalArgumentException, IllegalAccessException {
        if (value == null) {
            Nullable nullable = field.getAnnotation(Nullable.class);
            if (nullable == null)
                return false;
            Field isSetField = ReflectionUtils.findField(from.getClass(), nullable.value());
            if (isSetField == null)
                throw new IllegalStateException("There is no field " + nullable.value() + " in " + from.getClass());
            ReflectionUtils.makeAccessible(isSetField);
            if (isSetField.getBoolean(from))
                return true;
        }
        return false;
    }

    private boolean isFinalOrStatic(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers);
    }
}
