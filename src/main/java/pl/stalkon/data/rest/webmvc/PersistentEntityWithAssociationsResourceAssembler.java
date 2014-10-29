package pl.stalkon.data.rest.webmvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.PersistentEntityResource.Builder;
import org.springframework.data.rest.webmvc.mapping.AssociationLinks;
import org.springframework.data.rest.webmvc.support.Projector;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.util.Assert;

/**
 * Modification of {@link PersistentEntityResourceAssembler}. Linkable
 * associations don't need to have projections to be added to embedded wrapper.
 * Associations are wrapped recursively
 * 
 * @author Szymon Konicki
 *
 */
public class PersistentEntityWithAssociationsResourceAssembler extends PersistentEntityResourceAssembler {

	private final Repositories repositories;
	private final Projector projector;
	private final ResourceMappings mappings;
	private final EmbeddedWrappers wrappers = new EmbeddedWrappers(false);

	public PersistentEntityWithAssociationsResourceAssembler(Repositories repositories, EntityLinks entityLinks, Projector projector, ResourceMappings mappings) {
		super(repositories, entityLinks, projector, mappings);
		this.repositories = repositories;
		this.projector = projector;
		this.mappings = mappings;
	}

	@Override
	public PersistentEntityResource toResource(Object instance) {

		Assert.notNull(instance, "Entity instance must not be null!");

		return wrap(projector.projectExcerpt(instance), instance).build();

	}

	@Override
	public PersistentEntityResource toFullResource(Object instance) {
		Assert.notNull(instance, "Entity instance must not be null!");
		return wrap(projector.project(instance), instance).//
				renderAllAssociationLinks().build();
	}

	private Builder wrap(Object instance, Object source) {

		PersistentEntity<?, ?> entity = repositories.getPersistentEntity(source.getClass());

		return PersistentEntityResource.build(instance, entity).//
				withEmbedded(getEmbeddedResources(source)).//
				withLink(getSelfLinkFor(source));
	}

	/**
	 * Returns the embedded resources to render. This will add an
	 * {@link RelatedResource} for linkable associations.
	 * 
	 * @param instance
	 *            must not be {@literal null}.
	 * @return
	 */
	private Iterable<EmbeddedWrapper> getEmbeddedResources(Object instance) {

		Assert.notNull(instance, "Entity instance must not be null!");

		PersistentEntity<?, ?> entity = repositories.getPersistentEntity(instance.getClass());

		final List<EmbeddedWrapper> associationProjections = new ArrayList<EmbeddedWrapper>();
		final BeanWrapper<Object> wrapper = BeanWrapper.create(instance, null);
		final AssociationLinks associationLinks = new AssociationLinks(mappings);
		final ResourceMetadata metadata = mappings.getMappingFor(instance.getClass());

		entity.doWithAssociations(new SimpleAssociationHandler() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.springframework.data.mapping.SimpleAssociationHandler#
			 * doWithAssociation(org.springframework.data.mapping.Association)
			 */
			@Override
			public void doWithAssociation(Association<? extends PersistentProperty<?>> association) {

				PersistentProperty<?> property = association.getInverse();

				if (!associationLinks.isLinkableAssociation(property)) {
					return;
				}

				Object value = wrapper.getProperty(association.getInverse());

				// TODO: find a way to check if value is lazy loaded
				if (value == null || !value.getClass().equals(property.getActualType())) {
					return;
				}

				String rel = metadata.getMappingFor(property).getRel();

				if (value instanceof Collection) {

					Collection<?> collection = (Collection<?>) value;

					if (collection.isEmpty()) {
						return;
					}

					List<Object> nestedCollection = new ArrayList<Object>();

					for (Object element : collection) {
						if (element != null) {
							nestedCollection.add(projector.projectExcerpt(element));
						}
					}

					associationProjections.add(wrappers.wrap(nestedCollection, rel));

				} else {
					associationProjections.add(wrappers.wrap(toFullResource(value), rel));
				}
			}
		});

		return associationProjections;
	}

}
