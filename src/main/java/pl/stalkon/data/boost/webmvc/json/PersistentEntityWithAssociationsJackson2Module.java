package pl.stalkon.data.boost.webmvc.json;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.rest.core.UriToEntityConverter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.json.PersistentEntityJackson2Module;
import org.springframework.data.rest.webmvc.mapping.AssociationLinks;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;


public class PersistentEntityWithAssociationsJackson2Module extends PersistentEntityJackson2Module{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4578053600517973572L;
	
	public PersistentEntityWithAssociationsJackson2Module(
			ResourceMappings mappings, PersistentEntities entities,
			RepositoryRestConfiguration config, UriToEntityConverter converter) {
		super(mappings, entities, config, converter);
//		AssociationLinks associationLinks = new AssociationLinks(mappings);
//		setSerializerModifier(new NotFechedAssociationOmittingSerializerModifier(entities, associationLinks, config));
	}  
	
	/**
	 * {@link BeanSerializerModifier} to drop the property descriptors for not fetched associations.
	 * Modification of {@link AssociationOmittingSerializerModifier} to not drop fetched associations
	 *  
	 * @author Szymon Konicki
	 */
	public static class NotFechedAssociationOmittingSerializerModifier extends BeanSerializerModifier{
		private final PersistentEntities entities;
		private final RepositoryRestConfiguration configuration;
		private final AssociationLinks associationLinks;

		/**
		 * Creates a new {@link AssociationOmittingSerializerModifier} for the given {@link PersistentEntities},
		 * {@link AssociationLinks} and {@link RepositoryRestConfiguration}.
		 * 
		 * @param entities must not be {@literal null}.
		 * @param associationLinks must not be {@literal null}.
		 * @param configuration must not be {@literal null}.
		 */
		private NotFechedAssociationOmittingSerializerModifier(PersistentEntities entities, AssociationLinks associationLinks,
				RepositoryRestConfiguration configuration) {

			Assert.notNull(entities, "PersistentEntities must not be null!");
			Assert.notNull(associationLinks, "AssociationLinks must not be null!");
			Assert.notNull(configuration, "RepositoryRestConfiguration must not be null!");

			this.entities = entities;
			this.configuration = configuration;
			this.associationLinks = associationLinks;
		}

		/* 
		 * (non-Javadoc)
		 * @see com.fasterxml.jackson.databind.ser.BeanSerializerModifier#updateBuilder(com.fasterxml.jackson.databind.SerializationConfig, com.fasterxml.jackson.databind.BeanDescription, com.fasterxml.jackson.databind.ser.BeanSerializerBuilder)
		 */
		@Override
		public BeanSerializerBuilder updateBuilder(SerializationConfig config, BeanDescription beanDesc,
				BeanSerializerBuilder builder) {

			PersistentEntity<?, ?> entity = entities.getPersistentEntity(beanDesc.getBeanClass());

			if (entity == null) {
				return builder;
			}

			List<BeanPropertyWriter> result = new ArrayList<BeanPropertyWriter>();

			for (BeanPropertyWriter writer : builder.getProperties()) {

				// Skip exported associations
				PersistentProperty<?> persistentProperty = entity.getPersistentProperty(writer.getName());
				
				if (persistentProperty == null) {
					continue;
				}

				// Skip ids unless explicitly configured to expose
				if (persistentProperty.isIdProperty() && !configuration.isIdExposedFor(entity.getType())) {
					continue;
				}

				result.add(writer);
			}

			builder.setProperties(result);

			return builder;
		}
	}
}
