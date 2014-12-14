package openrest.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;

public class PersistentEntityResourceExtender {

	private PersistentEntityResource entityResource;
	private static final EmbeddedWrappers wrappers = new EmbeddedWrappers(false);
	private List<EmbeddedWrapper> embeddeds = new ArrayList<EmbeddedWrapper>();
	private PersistentEntityResourceAssembler entityResourceAssembler;

	public PersistentEntityResourceExtender(PersistentEntityResource entityResource, PersistentEntityResourceAssembler entityResourceAssembler) {
		this.entityResource = entityResource;
		this.entityResourceAssembler = entityResourceAssembler;
	}

	public void embedResource() {

	}

	public void embed(Object source, String rel, boolean isResource) {
		embeddeds.add(getEmbeddedWrapper(source, rel, isResource));
	}

	public PersistentEntityResource build() {
		if (embeddeds.size() == 0)
			return entityResource;
		embeddeds.addAll(entityResource.getEmbeddeds().getContent());
		return PersistentEntityResource.build(entityResource.getContent(), entityResource.getPersistentEntity())//
				.withEmbedded(embeddeds)//
				.withLink(entityResourceAssembler.getSelfLinkFor(entityResource.getContent())).build();

	}

	public EmbeddedWrapper getEmbeddedWrapper(Object source, String rel, boolean isResource) {
		if (!isResource || source instanceof Collection)
			return wrappers.wrap(source, rel);
		return wrappers.wrap(entityResourceAssembler.toFullResource(source),rel);
	}
}
