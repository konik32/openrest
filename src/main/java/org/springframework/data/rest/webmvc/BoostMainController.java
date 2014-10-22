package org.springframework.data.rest.webmvc;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.data.rest.webmvc.AbstractRepositoryRestController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.stalkon.data.boost.repository.BoostRepository;
import pl.stalkon.data.rest.webmvc.ParsedRequest;

@RepositoryRestController
public class BoostMainController extends AbstractRepositoryRestController
		implements ApplicationEventPublisherAware {

	private static final String BASE_MAPPING = "/{repository}";

	private final EntityLinks entityLinks;
	private final BoostRepository boostJpaRepository;
	private final ResourceMappings mappings;

	private ApplicationEventPublisher publisher;

	@Autowired
	public BoostMainController(EntityLinks entityLinks,
			PagedResourcesAssembler<Object> assembler,
			BoostRepository boostJpaRepository, ResourceMappings mappings) {
		super(assembler);
		this.boostJpaRepository = boostJpaRepository;
		this.entityLinks = entityLinks;
		this.mappings = mappings;
	}

	@ResponseBody
	@RequestMapping(value = { BASE_MAPPING + "/{id}" + "/{property}",
			BASE_MAPPING }, method = RequestMethod.GET, params = "filter")
	public Resources<?> getResourceWithFilterParameter(
			ParsedRequest specificationInformation,
			PersistentEntityResourceAssembler assembler, Pageable pageable,
			Sort sort) throws ResourceNotFoundException,
			HttpRequestMethodNotSupportedException {
		if (specificationInformation.getPropertyPath() != null
				&& !specificationInformation.getPropertyPath().isCollection())
			return processRequest(specificationInformation, assembler, null,
					null);
		else
			return processRequest(specificationInformation, assembler,
					pageable, sort);

	}

	@ResponseBody
	@RequestMapping(value = { BASE_MAPPING + "/{id}" + "/{property}",
			BASE_MAPPING }, method = RequestMethod.GET, params = "expand")
	public Resources<?> getResourceWithExpandParameter(
			ParsedRequest specificationInformation,
			PersistentEntityResourceAssembler assembler, Pageable pageable,
			Sort sort) throws ResourceNotFoundException,
			HttpRequestMethodNotSupportedException {

		if (specificationInformation.getPropertyPath() != null
				&& !specificationInformation.getPropertyPath().isCollection())
			return processRequest(specificationInformation, assembler, null,
					null);
		else
			return processRequest(specificationInformation, assembler,
					pageable, sort);

	}

	@ResponseBody
	@RequestMapping(value = { BASE_MAPPING + "/{id}" }, method = RequestMethod.GET, params = "expand")
	public Resources<?> getResource(ParsedRequest specificationInformation,
			PersistentEntityResourceAssembler assembler, Pageable pageable,
			Sort sort) throws ResourceNotFoundException,
			HttpRequestMethodNotSupportedException {
		return processRequest(specificationInformation, assembler, null, null);
	}

	private Resources<?> processRequest(ParsedRequest specificationInformation,
			PersistentEntityResourceAssembler assembler, Pageable pageable,
			Sort sort) throws ResourceNotFoundException,
			HttpRequestMethodNotSupportedException {

		Iterable<?> results = boostJpaRepository.findAll(
				specificationInformation.getPartTreeSpecification(),
				(Class<Object>) specificationInformation.getDomainClass(),
				pageable, sort);

		ResourceMetadata metadata = mappings
				.getMappingFor(specificationInformation.getDomainClass());
		SearchResourceMappings searchMappings = metadata
				.getSearchResourceMappings();
		List<Link> links = new ArrayList<Link>();

		if (searchMappings.isExported()) {
			links.add(entityLinks.linkFor(metadata.getDomainType())
					.slash(searchMappings.getPath())
					.withRel(searchMappings.getRel()));
		}

		Resources<?> resources = resultToResources(results, assembler);
		resources.add(links);
		return resources;
	}

	@Override
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
	}



}
