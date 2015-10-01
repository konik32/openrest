package org.springframework.data.rest.webmvc;

import static org.springframework.data.rest.webmvc.ControllerUtils.EMPTY_RESOURCE_LIST;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import pl.openrest.filters.webmvc.CountResponse;
import pl.openrest.filters.webmvc.QueryMethodExecutor;

@RepositoryRestController
public class FilterableSearchController extends AbstractRepositoryRestController {

    private static final String SEARCH = "/search";
    private static final String BASE_MAPPING = "/{repository}" + SEARCH;

    private final QueryMethodExecutor queryMethodExecutor;

    /**
     * Creates a new {@link RepositorySearchController} using the given {@link PagedResourcesAssembler}, {@link EntityLinks} and
     * {@link ResourceMappings}.
     * 
     * @param assembler
     *            must not be {@literal null}.
     * @param entityLinks
     *            must not be {@literal null}.
     * @param mappings
     *            must not be {@literal null}.
     */
    @Autowired
    public FilterableSearchController(PagedResourcesAssembler<Object> assembler, @NonNull QueryMethodExecutor queryMethodExecutor) {

        super(assembler);

        this.queryMethodExecutor = queryMethodExecutor;
    }

    /**
     * Executes the search with the given name.
     * 
     * @param request
     * @param repository
     * @param search
     * @param pageable
     * @return
     * @throws ResourceNotFoundException
     */
    @ResponseBody
    @RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.GET, params = "orest")
    public ResponseEntity<Object> executeSearch(RootResourceInformation resourceInformation, WebRequest request,
            @PathVariable String search, @RequestParam MultiValueMap<String, Object> parameters, DefaultedPageable pageable, Sort sort,
            PersistentEntityResourceAssembler assembler) {

        Object result = queryMethodExecutor.executeQueryMethod(resourceInformation, parameters, search, pageable, sort);
        // In Spring Data Rest RepositoryEntityController
        // Method method = checkExecutability(resourceInformation, search);
        // Object resources = executeQueryMethod(resourceInformation.getInvoker(), request, method, pageable, sort, assembler);

        // return new ResponseEntity<Object>(resources, HttpStatus.OK);
        return new ResponseEntity<Object>(resultToResources(result, assembler, null), HttpStatus.OK);
    }

    /**
     * Executes a query method and exposes the results in compact form.
     * 
     * @param resourceInformation
     * @param repository
     * @param method
     * @param pageable
     * @return
     */
    @ResponseBody
    @RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.GET, //
    produces = { "application/x-spring-data-compact+json" }, params = "orest")
    public ResourceSupport executeSearchCompact(RootResourceInformation resourceInformation, WebRequest request,
            @PathVariable String repository, @PathVariable String search, @RequestParam MultiValueMap<String, Object> parameters,
            DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler) {
        // In Spring Data Rest RepositoryEntityController
        // Method method = checkExecutability(resourceInformation, search);
        // Object resource = executeQueryMethod(resourceInformation.getInvoker(), request, method, pageable, sort, assembler);
        Object result = queryMethodExecutor.executeQueryMethod(resourceInformation, parameters, search, pageable, sort);
        Object resource = resultToResources(result, assembler, null);
        List<Link> links = new ArrayList<Link>();

        if (resource instanceof Resources && ((Resources<?>) resource).getContent() != null) {

            for (Object obj : ((Resources<?>) resource).getContent()) {
                if (null != obj && obj instanceof Resource) {
                    Resource<?> res = (Resource<?>) obj;
                    links.add(resourceLink(resourceInformation, res));
                }
            }

        } else if (resource instanceof Resource) {

            Resource<?> res = (Resource<?>) resource;
            links.add(resourceLink(resourceInformation, res));
        }

        return new Resources<Resource<?>>(EMPTY_RESOURCE_LIST, links);
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.GET, params = { "orest", "count" })
    public ResponseEntity<Object> executeSearchCount(RootResourceInformation resourceInformation,
            @RequestParam MultiValueMap<String, Object> parameters, @PathVariable String search) {
        Object result = queryMethodExecutor.executeCountQueryMethod(resourceInformation, parameters, search);
        return new ResponseEntity<Object>(new CountResponse(result), HttpStatus.OK);
    }

}
