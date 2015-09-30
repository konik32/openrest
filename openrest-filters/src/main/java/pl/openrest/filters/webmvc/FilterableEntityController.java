package pl.openrest.filters.webmvc;

import java.io.Serializable;

import org.springframework.data.querydsl.QSort;
import org.springframework.data.rest.webmvc.AbstractFilterablesController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.support.BackendId;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.query.PredicateContext;
import pl.openrest.filters.query.PredicateContextBuilderFactory;
import pl.openrest.filters.query.PredicateContextBuilderFactory.PredicateContextBuilder;
import pl.openrest.filters.repository.PredicateContextRepositoryInvoker;
import pl.openrest.predicate.parser.FilterTreeBuilder;

public class FilterableEntityController extends AbstractFilterablesController {

    private final static String BASE_MAPPING = "/{repository}";

    public FilterableEntityController(PagedResourcesAssembler<Object> pagedResourcesAssembler,
            PredicateContextBuilderFactory predicateContextBuilderFactory, FilterTreeBuilder filterTreeBuilder) {
        super(pagedResourcesAssembler, predicateContextBuilderFactory, filterTreeBuilder);
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET, params = "orest")
    public ResponseEntity<Object> executeSearch(RootResourceInformation resourceInformation, FilterableEntityInformation entityInfo,
            @RequestParam MultiValueMap<String, Object> parameters, DefaultedPageable pageable, QSort sort,
            PersistentEntityResourceAssembler assembler) {

        PredicateContextBuilder predicateContextBuilder = predicateContextBuilderFactory.create(entityInfo);

        addFilters(parameters, predicateContextBuilder);

        boolean addDefaultPageable = checkIfAddDefaultPageable(entityInfo.isDefaultedPageable());

        Iterable<Object> result = getResult(entityInfo.getRepositoryInvoker(), predicateContextBuilder.build(), pageable, sort,
                addDefaultPageable);
        return new ResponseEntity<Object>(resultToResources(result, assembler, null), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET, params = "orest")
    public ResponseEntity<Object> getFilteredCollectionResource(RootResourceInformation resourceInformation,
            FilterableEntityInformation entityInfo, @RequestParam MultiValueMap<String, Object> parameters, DefaultedPageable pageable,
            QSort sort, PersistentEntityResourceAssembler assembler) {

        PredicateContextBuilder predicateContextBuilder = predicateContextBuilderFactory.create(entityInfo);

        addFilters(parameters, predicateContextBuilder);
        predicateContextBuilder.withStaticFilters();

        boolean addDefaultPageable = checkIfAddDefaultPageable(entityInfo.isDefaultedPageable());

        Iterable<Object> result = getResult(entityInfo.getRepositoryInvoker(), predicateContextBuilder.build(), pageable, sort,
                addDefaultPageable);
        return new ResponseEntity<Object>(resultToResources(result, assembler, null), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.GET, params = "orest")
    public ResponseEntity<Object> getFilteredResource(RootResourceInformation resourceInformation, FilterableEntityInformation entityInfo,
            @RequestParam MultiValueMap<String, Object> parameters, @BackendId Serializable id, PersistentEntityResourceAssembler assembler) {

        PredicateContextBuilder predicateContextBuilder = predicateContextBuilderFactory.create(entityInfo);
        predicateContextBuilder.withId(id);
        predicateContextBuilder.withStaticFilters();
        addFilters(parameters, predicateContextBuilder);

        Object result = getResult(entityInfo.getRepositoryInvoker(), predicateContextBuilder.build());
        return new ResponseEntity<Object>(resultToResources(result, assembler, null), HttpStatus.OK);
    }

    private Object getResult(PredicateContextRepositoryInvoker invoker, PredicateContext predicateContext) {
        return invoker.invokeFindOne(predicateContext);
    }

}
