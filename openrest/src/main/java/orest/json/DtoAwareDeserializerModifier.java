package orest.json;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

import orest.dto.DtoDomainRegistry;
import orest.dto.DtoInformation;

import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.rest.core.UriToEntityConverter;
import org.springframework.data.rest.webmvc.mapping.AssociationLinks;
import org.springframework.hateoas.UriTemplate;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;

public class DtoAwareDeserializerModifier extends BeanDeserializerModifier {
	private static final TypeDescriptor URI_DESCRIPTOR = TypeDescriptor.valueOf(URI.class);
	private final AssociationLinks associationLinks;
	private final UriToEntityConverter converter;
	private final PersistentEntities repositories;
	private final DtoDomainRegistry dtoDomainRegistry;
	public DtoAwareDeserializerModifier(PersistentEntities repositories, UriToEntityConverter converter,
			AssociationLinks associationLinks, DtoDomainRegistry dtoDomainRegistry) {

		Assert.notNull(repositories, "Repositories must not be null!");
		Assert.notNull(converter, "UriToEntityConverter must not be null!");
		Assert.notNull(associationLinks, "AssociationLinks must not be null!");
		Assert.notNull(dtoDomainRegistry,"DtoDomainRegistry must not be null!");
		this.repositories = repositories;
		this.converter = converter;
		this.associationLinks = associationLinks;
		this.dtoDomainRegistry = dtoDomainRegistry;
	}
	
	@Override
	public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc,
			BeanDeserializerBuilder builder) {
		
		
		DtoInformation dtoInfo = dtoDomainRegistry.get(beanDesc.getBeanClass());
		if (dtoInfo == null) {
			return builder;
		}
		PersistentEntity<?, ?> entity = repositories.getPersistentEntity(dtoInfo.getEntityType());
		Iterator<SettableBeanProperty> properties = builder.getProperties();
		addUriDeserializers(builder, config, properties, entity);

		return builder;
	}

	private BeanDeserializerBuilder addUriDeserializers(BeanDeserializerBuilder builder, DeserializationConfig config,
			Iterator<SettableBeanProperty> properties, PersistentEntity<?, ?> entity) {
		if (entity == null) {
			return builder;
		}

		while (properties.hasNext()) {

			SettableBeanProperty property = properties.next();
			PersistentProperty<?> persistentProperty = entity.getPersistentProperty(property.getName());

			if (!associationLinks.isLinkableAssociation(persistentProperty)) {
				continue;
			}

			UriStringDeserializer uriStringDeserializer = new UriStringDeserializer(persistentProperty, converter);

			if (persistentProperty.isCollectionLike()) {

				CollectionLikeType collectionType = config.getTypeFactory().constructCollectionLikeType(persistentProperty.getType(),
						persistentProperty.getActualType());
				CollectionValueInstantiator instantiator = new CollectionValueInstantiator(persistentProperty);
				CollectionDeserializer collectionDeserializer = new CollectionDeserializer(collectionType, uriStringDeserializer, null, instantiator);

				builder.addOrReplaceProperty(property.withValueDeserializer(collectionDeserializer), false);

			} else {
				builder.addOrReplaceProperty(property.withValueDeserializer(uriStringDeserializer), false);
			}
		}

		return builder;
	}

	static class UriStringDeserializer extends StdDeserializer<Object> {

		private static final long serialVersionUID = -2175900204153350125L;

		private final PersistentProperty<?> property;
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
		public UriStringDeserializer(PersistentProperty<?> property, UriToEntityConverter converter) {

			super(property.getActualType());

			this.property = property;
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
		public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

			URI uri = new UriTemplate(jp.getValueAsString()).expand();
			TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(property.getActualType());

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

		private final PersistentProperty<?> property;

		/**
		 * Creates a new {@link CollectionValueInstantiator} for the given
		 * {@link PersistentProperty}.
		 * 
		 * @param property
		 *            must not be {@literal null} and must be a collection.
		 */
		public CollectionValueInstantiator(PersistentProperty<?> property) {

			Assert.notNull(property, "Property must not be null!");
			Assert.isTrue(property.isCollectionLike() || property.isMap(), "Property must be a collection or map property!");

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
			return property.getType().getName();
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

			Class<?> collectionOrMapType = property.getType();

			return property.isMap() ? CollectionFactory.createMap(collectionOrMapType, 0) : CollectionFactory.createCollection(collectionOrMapType, 0);
		}
	}
}
