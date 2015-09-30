package pl.openrest.filters.webmvc;

import lombok.NonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.rest.webmvc.AbstractFilterablesController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.predicate.registry.PredicateInformation;
import pl.openrest.filters.query.PredicateContextBuilderFactory;
import pl.openrest.filters.query.PredicateContextBuilderFactory.PredicateContextBuilder;
import pl.openrest.predicate.parser.FilterTreeBuilder;
import pl.openrest.predicate.parser.PredicateParts;
import pl.openrest.predicate.parser.PredicatePartsExtractor;

public class FilterableSearchController extends AbstractFilterablesController {

    private final PredicatePartsExtractor predicatePartsExtractor;

    private final static String BASE_MAPPING = "/{repository}/search";

    @Autowired
    public FilterableSearchController(PagedResourcesAssembler<Object> pagedResourcesAssembler,
            PredicateContextBuilderFactory predicateContextBuilderFactory, FilterTreeBuilder filterTreeBuilder,
            @NonNull PredicatePartsExtractor predicatePartsExtractor) {
        super(pagedResourcesAssembler, predicateContextBuilderFactory, filterTreeBuilder);
        this.predicatePartsExtractor = predicatePartsExtractor;
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.GET, params = "orest")
    public ResponseEntity<Object> executeSearch(RootResourceInformation resourceInformation, FilterableEntityInformation entityInfo,
            @RequestParam MultiValueMap<String, Object> parameters, @PathVariable String search, DefaultedPageable pageable, QSort sort,
            PersistentEntityResourceAssembler assembler) {

        PredicateParts searchPredicateParts = predicatePartsExtractor.extractParts(search);
        PredicateContextBuilder predicateContextBuilder = predicateContextBuilderFactory.create(entityInfo);

        predicateContextBuilder.withPredicateParts(searchPredicateParts);
        predicateContextBuilder.withStaticFilters();
        addFilters(parameters, predicateContextBuilder);

        PredicateInformation searchPredicateInfo = entityInfo.getPredicateInformation(searchPredicateParts.getPredicateName());
        boolean addDefaultPageable = checkIfAddDefaultPageable(entityInfo.isDefaultedPageable(), searchPredicateInfo.isDefaultedPageable());

        Iterable<Object> result = getResult(entityInfo.getRepositoryInvoker(), predicateContextBuilder.build(), pageable, sort,
                addDefaultPageable);
        return new ResponseEntity<Object>(resultToResources(result, assembler, null), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.GET, params = {"orest", "count"})
    public ResponseEntity<Object> executeSearchCount(RootResourceInformation resourceInformation, FilterableEntityInformation entityInfo,
            @RequestParam MultiValueMap<String, Object> parameters, @PathVariable String search) {

        PredicateParts searchPredicateParts = predicatePartsExtractor.extractParts(search);
        PredicateContextBuilder predicateContextBuilder = predicateContextBuilderFactory.create(entityInfo);

        predicateContextBuilder.withPredicateParts(searchPredicateParts);
        predicateContextBuilder.withStaticFilters();
        addFilters(parameters, predicateContextBuilder);

        Object result = getCountResult(entityInfo.getRepositoryInvoker(), predicateContextBuilder.build());
        return new ResponseEntity<Object>(new CountResponse(result), HttpStatus.OK);
    }

}
