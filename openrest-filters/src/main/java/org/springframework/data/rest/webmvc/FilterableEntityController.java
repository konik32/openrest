package org.springframework.data.rest.webmvc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.NonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.data.rest.webmvc.support.BackendId;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.openrest.filters.webmvc.CountResponse;
import pl.openrest.filters.webmvc.ResourceResolver;

@RepositoryRestController
public class FilterableEntityController extends AbstractRepositoryRestController {

    private static final String BASE_MAPPING = "/{repository}";

    private final RepositoryEntityLinks entityLinks;
    private final ResourceResolver resourceResolver;

    @Autowired
    public FilterableEntityController(Repositories repositories, RepositoryEntityLinks entityLinks,
            PagedResourcesAssembler<Object> assembler, @NonNull ResourceResolver resourceResolver) {

        super(assembler);

        this.entityLinks = entityLinks;
        this.resourceResolver = resourceResolver;
    }

    /**
     * <code>GET /{repository}</code> - Returns the collection resource (paged or unpaged).
     * 
     * @param resourceInformation
     * @param pageable
     * @param sort
     * @param assembler
     * @return
     * @throws ResourceNotFoundException
     * @throws HttpRequestMethodNotSupportedException
     */
    @ResponseBody
    @RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET, params = "orest")
    public Resources<?> getCollectionResource(final RootResourceInformation resourceInformation,
            @RequestParam MultiValueMap<String, Object> parameters, DefaultedPageable pageable, Sort sort,
            PersistentEntityResourceAssembler assembler) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

        resourceInformation.verifySupportedMethod(HttpMethod.GET, ResourceType.COLLECTION);

        Iterable<?> results = resourceResolver.getCollectionResource(resourceInformation, parameters, pageable, sort);

        // In Spring Data Rest RepositoryEntityController
        // RepositoryInvoker invoker = resourceInformation.getInvoker();
        //
        // if (null == invoker) {
        // throw new ResourceNotFoundException();
        // }
        //
        // Iterable<?> results;
        //
        // if (pageable.getPageable() != null) {
        // results = invoker.invokeFindAll(pageable.getPageable());
        // } else {
        // results = invoker.invokeFindAll(sort);
        // }

        ResourceMetadata metadata = resourceInformation.getResourceMetadata();
        SearchResourceMappings searchMappings = metadata.getSearchResourceMappings();
        List<Link> links = new ArrayList<Link>();

        if (searchMappings.isExported()) {
            links.add(entityLinks.linkFor(metadata.getDomainType()).slash(searchMappings.getPath()).withRel(searchMappings.getRel()));
        }

        Link baseLink = entityLinks.linkToPagedResource(resourceInformation.getDomainType(),
                pageable.isDefault() ? null : pageable.getPageable());

        Resources<?> resources = resultToResources(results, assembler, baseLink);
        resources.add(links);
        return resources;
    }

    @ResponseBody
    @SuppressWarnings({ "unchecked" })
    @RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET, produces = { "application/x-spring-data-compact+json",
            "text/uri-list" }, params = "orest")
    public Resources<?> getCollectionResourceCompact(RootResourceInformation repoRequest,
            @RequestParam MultiValueMap<String, Object> parameters, DefaultedPageable pageable, Sort sort,
            PersistentEntityResourceAssembler assembler) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

        Resources<?> resources = getCollectionResource(repoRequest, parameters, pageable, sort, assembler);

        List<Link> links = new ArrayList<Link>(resources.getLinks());

        for (Resource<?> resource : ((Resources<Resource<?>>) resources).getContent()) {
            PersistentEntityResource persistentEntityResource = (PersistentEntityResource) resource;
            links.add(resourceLink(repoRequest, persistentEntityResource));
        }
        if (resources instanceof PagedResources) {
            return new PagedResources<Object>(Collections.emptyList(), ((PagedResources<?>) resources).getMetadata(), links);
        } else {
            return new Resources<Object>(Collections.emptyList(), links);
        }
    }

    /**
     * <code>GET /{repository}/{id}</code> - Returns a single entity.
     * 
     * @param resourceInformation
     * @param id
     * @return
     * @throws HttpRequestMethodNotSupportedException
     */
    @RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.GET, params = "orest")
    public ResponseEntity<Resource<?>> getItemResource(RootResourceInformation resourceInformation, @BackendId Serializable id,
            @RequestParam MultiValueMap<String, Object> parameters, PersistentEntityResourceAssembler assembler)
            throws HttpRequestMethodNotSupportedException {

        Object domainObj = resourceResolver.getItemResource(resourceInformation, parameters, id);
        // In Spring Data Rest RepositoryEntityController
        // Object domainObj = getItemResource(resourceInformation, id);

        if (domainObj == null) {
            return new ResponseEntity<Resource<?>>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Resource<?>>(assembler.toFullResource(domainObj), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET, params = { "orest", "count" })
    public ResponseEntity<Object> getFilteredCollectionCount(RootResourceInformation resourceInformation,
            @RequestParam MultiValueMap<String, Object> parameters) {
        Object result = resourceResolver.getCollectionCount(resourceInformation, parameters);
        return new ResponseEntity<Object>(new CountResponse(result), HttpStatus.OK);
    }

}
