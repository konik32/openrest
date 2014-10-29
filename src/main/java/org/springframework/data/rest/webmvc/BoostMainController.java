package org.springframework.data.rest.webmvc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.data.rest.webmvc.AbstractRepositoryRestController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.stalkon.data.boost.repository.BoostRepository;
import pl.stalkon.data.rest.webmvc.PersistentEntityWithAssociationsResourceAssembler;
import pl.stalkon.data.rest.webmvc.ParsedRequest;

@RepositoryRestController
public class BoostMainController extends AbstractRepositoryRestController implements ApplicationEventPublisherAware {

	private static final String BASE_MAPPING = "/{repository}";

	private final RepositoryEntityLinks entityLinks;
	private final BoostRepository boostJpaRepository;
	private final ResourceMappings mappings;

	private ApplicationEventPublisher publisher;

	@Autowired
	public BoostMainController(RepositoryEntityLinks entityLinks, PagedResourcesAssembler<Object> assembler, BoostRepository boostJpaRepository,
			ResourceMappings mappings) {
		super(assembler);
		this.boostJpaRepository = boostJpaRepository;
		this.entityLinks = entityLinks;
		this.mappings = mappings;
	}

	@ResponseBody
	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET, params = "filter")
	public Resources<?> getResourceWithFilterParameter(ParsedRequest specificationInformation, PersistentEntityWithAssociationsResourceAssembler assembler,
			DefaultedPageable pageable, Sort sort){
		return getResources(specificationInformation, assembler, pageable, sort);
	}

	@ResponseBody
	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET, params = "expand")
	public Resources<?> getResourceWithExpandParameter(ParsedRequest specificationInformation, PersistentEntityResourceAssembler assembler,
			DefaultedPageable pageable, Sort sort) {
		return getResources(specificationInformation, assembler, pageable, sort);
	}

	@ResponseBody
	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET, params = { "filter", "expand" })
	public Resources<?> getResourceWithFilterAndExpandParameter(ParsedRequest specificationInformation, PersistentEntityWithAssociationsResourceAssembler assembler,
			DefaultedPageable pageable, Sort sort) {
		return getResources(specificationInformation, assembler, pageable, sort);
	}

	@ResponseBody
	@RequestMapping(value = BASE_MAPPING + "/{id}/{property}", method = RequestMethod.GET, params = "filter")
	public ResponseEntity<ResourceSupport> getResourcePropertyWithFilterParameter(ParsedRequest specificationInformation, PersistentEntityResourceAssembler assembler,
			DefaultedPageable pageable, Sort sort) {
		return getResourceSupporResponseEntity(specificationInformation, assembler, pageable, sort);
	}

	@ResponseBody
	@RequestMapping(value = BASE_MAPPING + "/{id}/{property}", method = RequestMethod.GET, params = "expand")
	public ResponseEntity<ResourceSupport> getResourcePropertyWithExpandParameter(ParsedRequest specificationInformation, PersistentEntityResourceAssembler assembler,
			DefaultedPageable pageable, Sort sort) {
		return getResourceSupporResponseEntity(specificationInformation, assembler, pageable, sort);
	}

	@ResponseBody
	@RequestMapping(value = BASE_MAPPING + "/{id}/{property}", method = RequestMethod.GET, params = { "filter", "expand" })
	public ResponseEntity<ResourceSupport> getResourcePropertyWithFilterAndExpandParameter(ParsedRequest specificationInformation, PersistentEntityResourceAssembler assembler,
			DefaultedPageable pageable, Sort sort) {

		return getResourceSupporResponseEntity(specificationInformation, assembler, pageable, sort);
	}


	@ResponseBody
	@RequestMapping(value = { BASE_MAPPING + "/{id}" }, method = RequestMethod.GET, params = "expand")
	public ResponseEntity<Resource<?>> getResourceWithExpandParameter(ParsedRequest specificationInformation, PersistentEntityWithAssociationsResourceAssembler assembler){
		Resource<?> resource = getResource(specificationInformation, assembler);
		if(resource == null){
			return new ResponseEntity<Resource<?>>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Resource<?>>(resource, HttpStatus.OK);
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
	}
	
	private ResponseEntity<ResourceSupport> getResourceSupporResponseEntity(ParsedRequest specificationInformation, PersistentEntityResourceAssembler assembler,
			DefaultedPageable pageable, Sort sort){
		
		final HttpHeaders headers = new HttpHeaders();
		ResourceSupport responseResource;
		if(specificationInformation.getPropertyPath().isCollection()){
			responseResource = getResources(specificationInformation, assembler, pageable, sort);
		}else{
			Resource<?> r = getResource(specificationInformation, assembler);
			if(r == null)
				return new ResponseEntity<ResourceSupport>(HttpStatus.NOT_FOUND);
			PersistentEntityResource resource = assembler.toFullResource(r.getContent());
			headers.set("Content-Location", resource.getId().getHref());
			responseResource = resource;
		}
		return ControllerUtils.toResponseEntity(HttpStatus.OK, headers, responseResource);
	}
	
	private Resources<?> getResources(ParsedRequest specificationInformation, PersistentEntityResourceAssembler assembler,
			DefaultedPageable pageable, Sort sort) {
		Iterable<?> results = findAll(specificationInformation, pageable, sort);
		
		ResourceMetadata metadata = mappings.getMappingFor(specificationInformation.getDomainClass());
		SearchResourceMappings searchMappings = metadata.getSearchResourceMappings();
		List<Link> links = new ArrayList<Link>();

		if (searchMappings.isExported()) {
			links.add(entityLinks.linkFor(metadata.getDomainType()).slash(searchMappings.getPath()).withRel(searchMappings.getRel()));
		}
		Resources<?> resources = resultToResources(results, assembler, null);
		resources.add(links);
		return resources;
	}
	
	private Resource<?> getResource(ParsedRequest specificationInformation, PersistentEntityResourceAssembler assembler){
		Iterable<?> results = findAll(specificationInformation, null, null);
		if (!results.iterator().hasNext()) {
			return null;
		}
		return assembler.toFullResource(results.iterator().next());
	}
	
	private Iterable<?> findAll(ParsedRequest specificationInformation, DefaultedPageable pageable, Sort sort) {
		return boostJpaRepository.findAll(specificationInformation.getPartTreeSpecification(), (Class<Object>) specificationInformation.getDomainClass(),
				pageable != null ? pageable.getPageable() : null, sort);
	}

}
