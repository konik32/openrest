package org.springframework.data.rest.webmvc;

import static org.springframework.data.rest.webmvc.ControllerUtils.EMPTY_RESOURCE_LIST;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.AuditableBeanWrapperFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.web.PagedResourcesAssembler;
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

import pl.openrest.filters.repository.PredicateContextRepositoryInvokerAdapter;
import pl.openrest.filters.webmvc.CountResponse;

@RepositoryRestController
public class FilterableSearchController extends AbstractRepositoryRestController {

    private static final String SEARCH = "/search";
    private static final String BASE_MAPPING = "/{repository}" + SEARCH;

    @Autowired
    public FilterableSearchController(PagedResourcesAssembler<Object> assembler, AuditableBeanWrapperFactory auditableBeanWrapperFactory) {

        super(assembler, auditableBeanWrapperFactory);

    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.GET, params = "orest")
    public ResponseEntity<Object> executeSearch(RootResourceInformation resourceInformation, WebRequest request,
            @PathVariable String search, @RequestParam MultiValueMap<String, Object> parameters, DefaultedPageable pageable, Sort sort,
            PersistentEntityResourceAssembler assembler) {

        RepositoryInvoker invoker = resourceInformation.getInvoker();
        Object result = invoker.invokeQueryMethod(null, parameters, pageable.getPageable(), sort);
        return new ResponseEntity<Object>(toResource(result, assembler, resourceInformation.getDomainType(), null), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.GET, //
    produces = { "application/x-spring-data-compact+json" }, params = "orest")
    public ResourceSupport executeSearchCompact(RootResourceInformation resourceInformation, WebRequest request,
            @PathVariable String repository, @PathVariable String search, @RequestParam MultiValueMap<String, Object> parameters,
            DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler) {
        RepositoryInvoker invoker = resourceInformation.getInvoker();
        Object result = invoker.invokeQueryMethod(null, parameters, pageable.getPageable(), sort);
        Object resource = toResource(result, assembler, resourceInformation.getDomainType(), null);
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
    public ResponseEntity<Object> executeSearchCount(RootResourceInformation resourceInformation) {
        PredicateContextRepositoryInvokerAdapter invoker = (PredicateContextRepositoryInvokerAdapter) resourceInformation.getInvoker();
        Object result = invoker.invokeCountQueryMethod();
        return new ResponseEntity<Object>(new CountResponse(result), HttpStatus.OK);
    }
    
    @ResponseBody
    @RequestMapping(value = "/{repository}", method = RequestMethod.GET, params = { "orest", "count" })
    public ResponseEntity<Object> executeCount(RootResourceInformation resourceInformation) {
        PredicateContextRepositoryInvokerAdapter invoker = (PredicateContextRepositoryInvokerAdapter) resourceInformation.getInvoker();
        Object result = invoker.invokeCountQueryMethod();
        return new ResponseEntity<Object>(new CountResponse(result), HttpStatus.OK);
    }

}
