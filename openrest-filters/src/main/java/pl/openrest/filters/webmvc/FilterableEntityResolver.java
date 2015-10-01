package pl.openrest.filters.webmvc;

import java.io.Serializable;
import java.util.List;

import lombok.NonNull;

import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.util.MultiValueMap;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
import pl.openrest.filters.predicate.registry.PredicateInformation;
import pl.openrest.filters.query.PredicateContext;
import pl.openrest.filters.query.PredicateContextBuilderFactory;
import pl.openrest.filters.query.PredicateContextBuilderFactory.PredicateContextBuilder;
import pl.openrest.filters.repository.PredicateContextRepositoryInvoker;
import pl.openrest.predicate.parser.FilterPart;
import pl.openrest.predicate.parser.FilterTreeBuilder;
import pl.openrest.predicate.parser.PredicateParts;
import pl.openrest.predicate.parser.PredicatePartsExtractor;

public class FilterableEntityResolver implements ResourceResolver, QueryMethodExecutor {

    public static final String FILTERS_PARAM_NAME = "filter";

    private final PredicateContextBuilderFactory predicateContextBuilderFactory;
    private final FilterableEntityRegistry filterableEntityRegistry;
    private final FilterTreeBuilder filterTreeBuilder;
    private final PredicatePartsExtractor predicatePartsExtractor;

    public FilterableEntityResolver(@NonNull PredicateContextBuilderFactory predicateContextBuilderFactory,
            @NonNull FilterableEntityRegistry filterableEntityRegistry, @NonNull FilterTreeBuilder filterTreeBuilder,
            @NonNull PredicatePartsExtractor predicatePartsExtractor) {
        this.predicateContextBuilderFactory = predicateContextBuilderFactory;
        this.filterableEntityRegistry = filterableEntityRegistry;
        this.filterTreeBuilder = filterTreeBuilder;
        this.predicatePartsExtractor = predicatePartsExtractor;
    }

    @Override
    public Iterable<Object> getCollectionResource(RootResourceInformation resourceInformation, MultiValueMap<String, Object> parameters,
            DefaultedPageable pageable, Sort sort) {

        FilterableEntityInformation entityInfo = getEntityInfor(resourceInformation.getDomainType());

        PredicateContext predicateContext = buildCollectionPredicateContext(resourceInformation, parameters, entityInfo);

        boolean addDefaultPageable = checkIfAddDefaultPageable(entityInfo.isDefaultedPageable());
        return getResult(entityInfo.getRepositoryInvoker(), predicateContext, pageable, sort, addDefaultPageable);
    }

    @Override
    public Object getItemResource(RootResourceInformation resourceInformation, MultiValueMap<String, Object> parameters, Serializable id) {

        FilterableEntityInformation entityInfo = getEntityInfor(resourceInformation.getDomainType());

        PredicateContextBuilder predicateContextBuilder = predicateContextBuilderFactory.create(entityInfo);
        predicateContextBuilder.withId(resourceInformation.getPersistentEntity().getIdProperty(), id);
        predicateContextBuilder.withStaticFilters();
        addFilters(parameters, predicateContextBuilder);

        return entityInfo.getRepositoryInvoker().invokeFindOne(predicateContextBuilder.build());
    }

    @Override
    public Object executeQueryMethod(RootResourceInformation resourceInformation, MultiValueMap<String, Object> parameters, String search,
            DefaultedPageable pageable, Sort sort) {

        FilterableEntityInformation entityInfo = getEntityInfor(resourceInformation.getDomainType());
        PredicateParts searchPredicateParts = predicatePartsExtractor.extractParts(search);

        PredicateContext predicateContext = buildQueryMethodPredicateContext(resourceInformation, parameters, searchPredicateParts,
                entityInfo);
        PredicateInformation searchPredicateInformation = entityInfo.getPredicateInformation(searchPredicateParts.getPredicateName());
        boolean addDefaultPageable = checkIfAddDefaultPageable(entityInfo.isDefaultedPageable(),
                searchPredicateInformation.isDefaultedPageable());
        return getResult(entityInfo.getRepositoryInvoker(), predicateContext, pageable, sort, addDefaultPageable);
    }

    @Override
    public Object executeCountQueryMethod(RootResourceInformation resourceInformation, MultiValueMap<String, Object> parameters,
            String search) {
        FilterableEntityInformation entityInfo = getEntityInfor(resourceInformation.getDomainType());
        PredicateParts searchPredicateParts = predicatePartsExtractor.extractParts(search);
        PredicateContext predicateContext = buildQueryMethodPredicateContext(resourceInformation, parameters, searchPredicateParts,
                entityInfo);

        return entityInfo.getRepositoryInvoker().invokeCount(predicateContext);
    }

    @Override
    public Object getCollectionCount(RootResourceInformation resourceInformation, MultiValueMap<String, Object> parameters) {
        FilterableEntityInformation entityInfo = getEntityInfor(resourceInformation.getDomainType());

        PredicateContext predicateContext = buildCollectionPredicateContext(resourceInformation, parameters, entityInfo);

        return entityInfo.getRepositoryInvoker().invokeCount(predicateContext);
    }

    private PredicateContext buildQueryMethodPredicateContext(RootResourceInformation resourceInformation,
            MultiValueMap<String, Object> parameters, PredicateParts searchPredicateParts, FilterableEntityInformation entityInfo) {

        PredicateContextBuilder predicateContextBuilder = predicateContextBuilderFactory.create(entityInfo);
        predicateContextBuilder.withPredicateParts(searchPredicateParts);
        predicateContextBuilder.withStaticFilters();
        addFilters(parameters, predicateContextBuilder);
        return predicateContextBuilder.build();
    }

    private PredicateContext buildCollectionPredicateContext(RootResourceInformation resourceInformation,
            MultiValueMap<String, Object> parameters, FilterableEntityInformation entityInfo) {
        PredicateContextBuilder predicateContextBuilder = predicateContextBuilderFactory.create(entityInfo);
        predicateContextBuilder.withStaticFilters();
        addFilters(parameters, predicateContextBuilder);
        return predicateContextBuilder.build();
    }

    private void addFilters(MultiValueMap<String, Object> parameters, PredicateContextBuilder builder) {
        List<Object> filterParams = parameters.get(FILTERS_PARAM_NAME);
        if (filterParams == null)
            return;
        for (Object filterParam : filterParams) {
            FilterPart filterTree = filterTreeBuilder.from((String) filterParam);
            builder.withFilterTree(filterTree);
        }
    }

    private FilterableEntityInformation getEntityInfor(Class<?> entityType) {
        FilterableEntityInformation entityInfo = filterableEntityRegistry.get(entityType);
        if (entityInfo == null)
            throw new IllegalArgumentException("Could not resolve FilterableEntityInformation");
        return entityInfo;
    }

    private boolean checkIfAddDefaultPageable(boolean entityInfoPageable, boolean searchPredicatePageable) {
        if (!entityInfoPageable)
            return false;
        return searchPredicatePageable;
    }

    private boolean checkIfAddDefaultPageable(boolean entityInfoPageable) {
        return checkIfAddDefaultPageable(entityInfoPageable, true);
    }

    private Iterable<Object> getResult(PredicateContextRepositoryInvoker invoker, PredicateContext predicateContext,
            DefaultedPageable pageable, Sort sort, boolean addDefaultPageable) {
        Iterable<Object> result;
        if (pageable.getPageable() == null || (pageable.isDefault() && !addDefaultPageable))
            result = invoker.invokeFindAll(predicateContext, sort);
        else
            result = invoker.invokeFindAll(predicateContext, pageable.getPageable());
        return result;
    }

}