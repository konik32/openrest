package org.springframework.data.rest.webmvc;

import java.util.List;

import lombok.NonNull;

import org.springframework.data.querydsl.QSort;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.util.MultiValueMap;

import pl.openrest.filters.query.PredicateContext;
import pl.openrest.filters.query.PredicateContextBuilderFactory;
import pl.openrest.filters.query.PredicateContextBuilderFactory.PredicateContextBuilder;
import pl.openrest.filters.repository.PredicateContextRepositoryInvoker;
import pl.openrest.predicate.parser.FilterPart;
import pl.openrest.predicate.parser.FilterTreeBuilder;

public abstract class AbstractFilterablesController extends AbstractRepositoryRestController {

    public static final String FILTERS_PARAM_NAME = "filter";

    protected final PredicateContextBuilderFactory predicateContextBuilderFactory;
    protected final FilterTreeBuilder filterTreeBuilder;

    public AbstractFilterablesController(PagedResourcesAssembler<Object> pagedResourcesAssembler,
            @NonNull PredicateContextBuilderFactory predicateContextBuilderFactory, @NonNull FilterTreeBuilder filterTreeBuilder) {
        super(pagedResourcesAssembler);
        this.predicateContextBuilderFactory = predicateContextBuilderFactory;
        this.filterTreeBuilder = filterTreeBuilder;
    }

    protected void addFilters(MultiValueMap<String, Object> parameters, PredicateContextBuilder builder) {
        List<Object> filterParams = parameters.get(FILTERS_PARAM_NAME);
        for (Object filterParam : filterParams) {
            FilterPart filterTree = filterTreeBuilder.from((String) filterParam);
            builder.withFilterTree(filterTree);
        }
    }

    protected Iterable<Object> getResult(PredicateContextRepositoryInvoker invoker, PredicateContext predicateContext,
            DefaultedPageable pageable, QSort sort, boolean addDefaultPageable) {
        Iterable<Object> result;
        if (pageable.getPageable() == null || (pageable.isDefault() && !addDefaultPageable))
            result = invoker.invokeFindAll(predicateContext, sort);
        else
            result = invoker.invokeFindAll(predicateContext, pageable.getPageable());
        return result;
    }

    protected Object getCountResult(PredicateContextRepositoryInvoker invoker, PredicateContext predicateContext) {
        return invoker.invokeCount(predicateContext);
    }

    protected boolean checkIfAddDefaultPageable(boolean entityInfoPageable, boolean searchPredicatePageable) {
        if (!entityInfoPageable)
            return false;
        return searchPredicatePageable;
    }

    protected boolean checkIfAddDefaultPageable(boolean entityInfoPageable) {
        return checkIfAddDefaultPageable(entityInfoPageable, true);
    }
}
