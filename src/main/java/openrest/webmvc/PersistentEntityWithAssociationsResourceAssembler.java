package openrest.webmvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import openrest.dto.DtoPopulatorEvent;
import openrest.security.validator.ResourceFilterInvoker;

import org.hibernate.collection.internal.PersistentBag;
import org.springframework.context.ApplicationEventPublisher;
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
 * Modification of {@link PersistentEntityResourceAssembler}. Dto system was
 * implemented instead of projections
 * 
 * @author Szymon Konicki
 *
 */
public class PersistentEntityWithAssociationsResourceAssembler extends PersistentEntityResourceAssembler {

	private final Repositories repositories;
	private final ResourceMappings mappings;
	private final EmbeddedWrappers wrappers = new EmbeddedWrappers(false);

	private final ApplicationEventPublisher publisher;
	private final String[] dtos;

	private ResourceFilterInvoker resourceFilterInvoker;

	private Set<Object> currProcessedObjects = new HashSet<Object>();

	public PersistentEntityWithAssociationsResourceAssembler(Repositories repositories, EntityLinks entityLinks, Projector projector,
			ResourceMappings mappings, ApplicationEventPublisher publisher, String[] dtos) {
		super(repositories, entityLinks, projector, mappings);
		this.repositories = repositories;
		this.mappings = mappings;
		this.publisher = publisher;
		this.dtos = dtos;
	}

	@Override
	public PersistentEntityResource toResource(Object instance) {
		return toFullResource(instance);
	}

	@Override
	public PersistentEntityResource toFullResource(Object instance) {
		return toFullResource(instance, null);
	}

	public PersistentEntityResource toFullResource(Object instance, ParentAwareObject parent) {
		if (!include(instance, parent))
			return null;
		if (currProcessedObjects.contains(instance))
			return null;
		currProcessedObjects.add(instance);
		Assert.notNull(instance, "Entity instance must not be null!");
		PersistentEntityResource pe = wrap(instance, instance, parent).//
				renderAllAssociationLinks().build();
		currProcessedObjects.remove(instance);
		return pe;
	}

	private Builder wrap(Object instance, Object source, ParentAwareObject parent) {
		PersistentEntity<?, ?> entity = repositories.getPersistentEntity(source.getClass());
		return PersistentEntityResource.build(instance, entity).//
				withEmbedded(getEmbeddeds(source, parent)).//
				withLink(getSelfLinkFor(source));
	}

	private Iterable<EmbeddedWrapper> getEmbeddeds(Object instance, ParentAwareObject parent) {
		List<EmbeddedWrapper> embeddeds = getAssociationsEmbeddedResources(instance, parent);
		if (dtos != null)
			embeddeds.addAll(getDtoEmbeddeds(instance));
		return embeddeds;
	}

	private List<EmbeddedWrapper> getDtoEmbeddeds(Object instance) {
		List<EmbeddedWrapper> embeddeds = new ArrayList<EmbeddedWrapper>();
		publisher.publishEvent(new DtoPopulatorEvent(instance, embeddeds, dtos));
		return embeddeds;
	}

	/**
	 * Returns the embedded resources to render. This will add an
	 * {@link RelatedResource} for linkable associations.
	 * 
	 * @param instance
	 *            must not be {@literal null}.
	 * @return
	 */
	private List<EmbeddedWrapper> getAssociationsEmbeddedResources(final Object instance, final ParentAwareObject parent) {

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
				if (value instanceof PersistentBag) {
					if (!((PersistentBag) value).wasInitialized())
						return;
				} else {
					if (value == null || !value.getClass().equals(property.getActualType())) {
						return;
					}
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
							ParentAwareObject parentAwareObject = new ParentAwareObject(parent, instance);
							EmbeddedWrapper ew = wrappers.wrap(toFullResource(element, parentAwareObject));
							if (ew != null)
								nestedCollection.add(ew);
						}
					}
					if (!nestedCollection.isEmpty())
						associationProjections.add(wrappers.wrap(nestedCollection, rel));

				} else {
					ParentAwareObject parentAwareObject = new ParentAwareObject(parent, instance);
					EmbeddedWrapper ew = wrappers.wrap(toFullResource(value, parentAwareObject), rel);
					if (ew != null)
						associationProjections.add(ew);
				}
			}
		});

		return associationProjections;
	}

	public boolean include(Object instance, ParentAwareObject parent) {
		if (resourceFilterInvoker == null)
			return true;
		return resourceFilterInvoker.includeResource(instance, parent);
	}

	public void setResourceFilterInvoker(ResourceFilterInvoker resourceFilterInvoker) {
		this.resourceFilterInvoker = resourceFilterInvoker;
	}

}
