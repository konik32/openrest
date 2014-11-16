package org.springframework.data.rest.webmvc;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import openrest.httpquery.parser.RequestParsingException;
import openrest.jpa.repository.PartTreeSpecificationRepository;
import openrest.webmvc.ParsedRequest;
import openrest.webmvc.ParsedRequestHandlerMethodArgumentResolver;
import openrest.webmvc.PersistentEntityWithAssociationsResourceAssembler;
import openrest.webmvc.support.OpenRestEntityLinks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.data.rest.webmvc.AbstractRepositoryRestController;
import org.springframework.data.rest.webmvc.ControllerUtils;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.ExceptionMessage;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

@RepositoryRestController
public class OpenRestMainController extends AbstractRepositoryRestController implements ApplicationEventPublisherAware {

	private static final String BASE_MAPPING = "/{repository}";

	private final OpenRestEntityLinks entityLinks;
	private final PartTreeSpecificationRepository boostJpaRepository;
	private final ResourceMappings mappings;
	private final ParsedRequestHandlerMethodArgumentResolver resolver;
	private ApplicationEventPublisher publisher;

	@Autowired
	public OpenRestMainController(OpenRestEntityLinks entityLinks, PagedResourcesAssembler<Object> assembler,
			PartTreeSpecificationRepository boostJpaRepository, ResourceMappings mappings, ParsedRequestHandlerMethodArgumentResolver resolver) {
		super(assembler);
		this.boostJpaRepository = boostJpaRepository;
		this.entityLinks = entityLinks;
		this.mappings = mappings;
		this.resolver = resolver;
	}

	@ResponseBody
	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET, params = "orest")
	public Resources<?> getResourceWithFilterParameter(ParsedRequest specificationInformation, PersistentEntityWithAssociationsResourceAssembler assembler,
			DefaultedPageable pageable, Sort sort) {
		return getResources(specificationInformation, assembler, pageable, sort);
	}

	@ResponseBody
	@RequestMapping(value = BASE_MAPPING + "/{id}/{property}", method = RequestMethod.GET, params = "orest")
	public ResponseEntity<ResourceSupport> getResourcePropertyWithFilterParameter(ParsedRequest specificationInformation,
			PersistentEntityResourceAssembler assembler, DefaultedPageable pageable, Sort sort) {
		return getResourceSupporResponseEntity(specificationInformation, assembler, pageable, sort);
	}

	@ResponseBody
	@RequestMapping(value = { BASE_MAPPING + "/{id}" }, method = RequestMethod.GET, params = "orest")
	public ResponseEntity<Resource<?>> getResourceWithExpandParameter(ParsedRequest specificationInformation,
			PersistentEntityWithAssociationsResourceAssembler assembler) {
		Resource<?> resource = getResource(specificationInformation, assembler);
		if (resource == null) {
			return new ResponseEntity<Resource<?>>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Resource<?>>(resource, HttpStatus.OK);
	}

	@ExceptionHandler(value = { RequestParsingException.class, PropertyReferenceException.class })
	@ResponseBody
	public ResponseEntity<ExceptionMessage> handleParsingException(Exception e) {
		return badRequest(e);
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
	}

	private ResponseEntity<ResourceSupport> getResourceSupporResponseEntity(ParsedRequest specificationInformation,
			PersistentEntityResourceAssembler assembler, DefaultedPageable pageable, Sort sort) {

		final HttpHeaders headers = new HttpHeaders();
		ResourceSupport responseResource;
		if (specificationInformation.getPropertyPath().isCollection()) {
			responseResource = getResources(specificationInformation, assembler, pageable, sort);
		} else {
			Resource<?> r = getResource(specificationInformation, assembler);
			if (r == null)
				return new ResponseEntity<ResourceSupport>(HttpStatus.NOT_FOUND);
			PersistentEntityResource resource = assembler.toFullResource(r.getContent());
			headers.set("Content-Location", resource.getId().getHref());
			responseResource = resource;
		}
		return ControllerUtils.toResponseEntity(HttpStatus.OK, headers, responseResource);
	}

	private Resources<?> getResources(ParsedRequest specificationInformation, PersistentEntityResourceAssembler assembler, DefaultedPageable pageable, Sort sort) {
		Iterable<?> results = findAll(specificationInformation, pageable, sort);

		ResourceMetadata metadata = mappings.getMappingFor(specificationInformation.getDomainClass());
		SearchResourceMappings searchMappings = metadata.getSearchResourceMappings();
		List<Link> links = new ArrayList<Link>();

		if (searchMappings.isExported()) {
			links.add(entityLinks.linkFor(metadata.getDomainType()).slash(searchMappings.getPath()).withRel(searchMappings.getRel()));
		}
		String currentURL;
		try {
			currentURL = UriUtils.decode(ServletUriComponentsBuilder.fromCurrentRequest().build().toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
		Link baseLink = entityLinks.getSelfLink(currentURL, true);
		Resources<?> resources = resultToResources(results, assembler, baseLink);
		resources.add(links);
		return resources;
	}

	private Resource<?> getResource(ParsedRequest specificationInformation, PersistentEntityResourceAssembler assembler) {
		Iterable<?> results = findAll(specificationInformation, null, null);
		if (!results.iterator().hasNext()) {
			return null;
		}
		PersistentEntityResource resource = assembler.toFullResource(results.iterator().next());
		return resource;
	}

	private Iterable<?> findAll(ParsedRequest specificationInformation, DefaultedPageable pageable, Sort sort) {
		return boostJpaRepository.findAll(specificationInformation.getPartTreeSpecification(), (Class<Object>) specificationInformation.getDomainClass(),
				pageable != null ? pageable.getPageable() : null, sort);
	}

}
