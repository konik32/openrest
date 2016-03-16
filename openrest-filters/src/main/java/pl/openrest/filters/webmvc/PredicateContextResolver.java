package pl.openrest.filters.webmvc;

import java.io.Serializable;
import java.util.List;

import lombok.NonNull;

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.util.MultiValueMap;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.predicate.PredicateInformation;
import pl.openrest.filters.query.PredicateContext;
import pl.openrest.filters.query.PredicateContextBuilder;
import pl.openrest.filters.query.PredicateContextBuilderFactory;
import pl.openrest.predicate.parser.FilterPart;
import pl.openrest.predicate.parser.FilterTreeBuilder;
import pl.openrest.predicate.parser.PredicateParts;
import pl.openrest.predicate.parser.PredicatePartsExtractor;

public class PredicateContextResolver {

    public static final String FILTERS_PARAM_NAME = "filter";

    private final PredicateContextBuilderFactory<?> predicateContextBuilderFactory;
    private final FilterTreeBuilder filterTreeBuilder;
    private final PredicatePartsExtractor predicatePartsExtractor;

    public PredicateContextResolver(@NonNull PredicateContextBuilderFactory<?> predicateContextBuilderFactory,
            @NonNull FilterTreeBuilder filterTreeBuilder, @NonNull PredicatePartsExtractor predicatePartsExtractor) {
        this.predicateContextBuilderFactory = predicateContextBuilderFactory;
        this.filterTreeBuilder = filterTreeBuilder;
        this.predicatePartsExtractor = predicatePartsExtractor;
    }

    public PredicateContext<?> resolve(FilterableEntityInformation entityInfo, MultiValueMap<String, String> parameters) {
        return buildCollectionPredicateContext(parameters, entityInfo);
    }

    public PredicateContext<?> resolve(FilterableEntityInformation entityInfo, MultiValueMap<String, String> parameters,
            PersistentProperty idProperty, Serializable id) {

        PredicateContextBuilder predicateContextBuilder = predicateContextBuilderFactory.create(entityInfo);
        predicateContextBuilder.withId(idProperty, id);
        predicateContextBuilder.withStaticFilters();
        addFilters(parameters, predicateContextBuilder);
        return predicateContextBuilder.build();
    }

    public PredicateContext<?> resolve(FilterableEntityInformation entityInfo, MultiValueMap<String, String> parameters, String search) {

        PredicateParts searchPredicateParts = extractParts(search);

        PredicateContext<?> predicateContext = buildQueryMethodPredicateContext(parameters, searchPredicateParts, entityInfo);

        return predicateContext;
    }

    private PredicateParts extractParts(String search) {
        return predicatePartsExtractor.extractParts(search);
    }

    private PredicateContext<?> buildQueryMethodPredicateContext(MultiValueMap<String, String> parameters,
            PredicateParts searchPredicateParts, FilterableEntityInformation entityInfo) {

        PredicateContextBuilder predicateContextBuilder = predicateContextBuilderFactory.create(entityInfo);
        predicateContextBuilder.withPredicateParts(searchPredicateParts);
        predicateContextBuilder.withStaticFilters();
        addFilters(parameters, predicateContextBuilder);
        return predicateContextBuilder.build();
    }

    private PredicateContext<?> buildCollectionPredicateContext(MultiValueMap<String, String> parameters,
            FilterableEntityInformation entityInfo) {
        PredicateContextBuilder predicateContextBuilder = predicateContextBuilderFactory.create(entityInfo);
        predicateContextBuilder.withStaticFilters();
        addFilters(parameters, predicateContextBuilder);
        return predicateContextBuilder.build();
    }

    private void addFilters(MultiValueMap<String, String> parameters, PredicateContextBuilder builder) {
        List<String> filterParams = parameters.get(FILTERS_PARAM_NAME);
        if (filterParams == null)
            return;
        for (String filterParam : filterParams) {
            FilterPart filterTree = filterTreeBuilder.from(filterParam);
            builder.withFilterTree(filterTree);
        }
    }

    public boolean addDefaultPageable(FilterableEntityInformation entityInfo, String search) {
        if (!entityInfo.getPredicateRepository().isDefaultedPageable())
            return false;
        PredicateParts searchPredicateParts = extractParts(search);
        PredicateInformation searchPredicateInformation = entityInfo.getPredicateRepository().getPredicateInformation(
                searchPredicateParts.getPredicateName());
        return searchPredicateInformation.isDefaultedPageable();
    }

    public boolean addDefaultPageable(FilterableEntityInformation entityInfo) {
        return entityInfo.getPredicateRepository().isDefaultedPageable();
    }

}
