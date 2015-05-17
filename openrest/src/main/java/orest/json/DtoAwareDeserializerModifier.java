package orest.json;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import orest.dto.DtoDomainRegistry;
import orest.dto.DtoInformation;

import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.rest.core.UriToEntityConverter;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.hateoas.UriTemplate;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;

public class DtoAwareDeserializerModifier extends BeanDeserializerModifier {
	private static final TypeDescriptor URI_DESCRIPTOR = TypeDescriptor.valueOf(URI.class);
	private final ResourceMappings resourceMappings;
	private final UriToEntityConverter converter;
	private final PersistentEntities repositories;
	private final DtoDomainRegistry dtoDomainRegistry;

	public DtoAwareDeserializerModifier(PersistentEntities repositories, UriToEntityConverter converter,
			ResourceMappings resourceMappings, DtoDomainRegistry dtoDomainRegistry) {

		Assert.notNull(repositories, "Repositories must not be null!");
		Assert.notNull(converter, "UriToEntityConverter must not be null!");
		Assert.notNull(resourceMappings, "ResourceMappings must not be null!");
		Assert.notNull(dtoDomainRegistry, "DtoDomainRegistry must not be null!");
		this.repositories = repositories;
		this.converter = converter;
		this.resourceMappings = resourceMappings;
		this.dtoDomainRegistry = dtoDomainRegistry;
	}

	@Override
	public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc,
			BeanDeserializerBuilder builder) {

		DtoInformation dtoInfo = dtoDomainRegistry.get(beanDesc.getBeanClass());
		if (dtoInfo == null) {
			return builder;
		}
//		PersistentEntity<?, ?> entity = repositories.getPersistentEntity(dtoInfo.getEntityType());
		Iterator<SettableBeanProperty> properties = builder.getProperties();
		addUriDeserializers(builder, config, properties,beanDesc);

		return builder;
	}

	private BeanDeserializerBuilder addUriDeserializers(BeanDeserializerBuilder builder, DeserializationConfig config,
			Iterator<SettableBeanProperty> properties,BeanDescription beanDesc) {

		while (properties.hasNext()) {

			SettableBeanProperty property = properties.next();
			JavaType propertyJavaType = property.getType();
			Class<?> actualType = getActualType(propertyJavaType);

			if (!isLinkableAssociation(actualType)) {
				continue;
			}

			UriStringDeserializer uriStringDeserializer = new UriStringDeserializer(actualType, converter);

			if (propertyJavaType.isContainerType()) {

				CollectionLikeType collectionType = config.getTypeFactory().constructCollectionLikeType(
						propertyJavaType.getRawClass(), propertyJavaType.getContentType().getRawClass());
				CollectionValueInstantiator instantiator = new CollectionValueInstantiator(property);
				CollectionDeserializer collectionDeserializer = new CollectionDeserializer(collectionType,
						uriStringDeserializer, null, instantiator);

				builder.addOrReplaceProperty(property.withValueDeserializer(collectionDeserializer), false);
			} else {
				builder.addOrReplaceProperty(property.withValueDeserializer(uriStringDeserializer), false);
			}
		}

		return builder;
	}

	/**
	 * Returns whether the given property is an association that is linkable.
	 * 
	 * @param property
	 *            can be {@literal null}.
	 * @return
	 */
	private boolean isLinkableAssociation(Class<?> type) {
		ResourceMetadata metadata = resourceMappings.getMappingFor(type);
		return metadata == null ? false : metadata.isExported();
	}

	
	private Class<?> getActualType(JavaType propertyJavaType){
		if (propertyJavaType.isCollectionLikeType() || propertyJavaType.isArrayType()) {
			return propertyJavaType.getContentType().getRawClass();
		}else{
			return propertyJavaType.getRawClass();
		}
	}
	static class UriStringDeserializer extends StdDeserializer<Object> {

		private static final long serialVersionUID = -2175900204153350125L;

		private final Class<?> type;
		private final UriToEntityConverter converter;

		/**
		 * Creates a new {@link UriStringDeserializer} for the given
		 * {@link PersistentProperty} using the given
		 * {@link UriToEntityConverter}.
		 * 
		 * @param property
		 *            must not be {@literal null}.
		 * @param converter
		 *            must not be {@literal null}.
		 */
		public UriStringDeserializer(Class<?> type, UriToEntityConverter converter) {
			super(type);

			this.type = type;
			this.converter = converter;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml
		 * .jackson.core.JsonParser,
		 * com.fasterxml.jackson.databind.DeserializationContext)
		 */
		@Override
		public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
				JsonProcessingException {

			URI uri = new UriTemplate(jp.getValueAsString()).expand();
			TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(type);

			return converter.convert(uri, URI_DESCRIPTOR, typeDescriptor);
		}
	}

	/**
	 * {@link ValueInstantiator} to create collection or map instances based on
	 * the type of the configured {@link PersistentProperty}.
	 * 
	 * @author Oliver Gierke
	 */
	private static class CollectionValueInstantiator extends ValueInstantiator {

		private final SettableBeanProperty property;

		/**
		 * Creates a new {@link CollectionValueInstantiator} for the given
		 * {@link PersistentProperty}.
		 * 
		 * @param property
		 *            must not be {@literal null} and must be a collection.
		 */
		public CollectionValueInstantiator(SettableBeanProperty property) {

			Assert.notNull(property, "Property must not be null!");
			Assert.isTrue(property.getType().isContainerType(),
					"Property must be a collection or map property!");

			this.property = property;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.fasterxml.jackson.databind.deser.ValueInstantiator#getValueTypeDesc
		 * ()
		 */
		@Override
		public String getValueTypeDesc() {
			return property.getType().getRawClass().getName();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.fasterxml.jackson.databind.deser.ValueInstantiator#createUsingDefault
		 * (com.fasterxml.jackson.databind.DeserializationContext)
		 */
		@Override
		public Object createUsingDefault(DeserializationContext ctxt) throws IOException, JsonProcessingException {

			Class<?> collectionOrMapType = property.getType().getRawClass();

			return property.getType().isMapLikeType() ? CollectionFactory.createMap(collectionOrMapType, 0) : CollectionFactory
					.createCollection(collectionOrMapType, 0);
		}
	}
}
