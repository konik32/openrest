package pl.openrest.filters.webmvc;

import org.springframework.core.MethodParameter;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
import pl.openrest.filters.query.PredicateContext;
import pl.openrest.filters.repository.PredicateContextRepository;
import pl.openrest.filters.repository.PredicateContextRepositoryInvokerAdapter;

public class SearchPredicateRepositoryInvokerResolver extends AbstractFiltersRepositoryInvokerResolver {

    private static final String SEARCH_URI_TEMPLATE = "search";

    public SearchPredicateRepositoryInvokerResolver(FilterableEntityRegistry filterableEntityRegistry,
            PredicateContextResolver predicateContextResolver) {
        super(filterableEntityRegistry, predicateContextResolver);
    }

    @Override
    public boolean support(Class<?> domainType, NativeWebRequest webRequest) {
        if (super.support(domainType, webRequest)) {
            return extractUriTemplates(webRequest).containsKey(SEARCH_URI_TEMPLATE);
        }
        return false;
    }

    @Override
    public RepositoryInvoker resolve(RootResourceInformation information, Object repository, MethodParameter parameter,
            NativeWebRequest webRequest) {
        FilterableEntityInformation entityInfo = filterableEntityRegistry.get(information.getDomainType());
        String search = extractUriTemplates(webRequest).get(SEARCH_URI_TEMPLATE);
        PredicateContext<?> predicateContext = predicateContextResolver.resolve(entityInfo, toMultiValueMap(webRequest.getParameterMap()),
                search);

        PredicateContextRepositoryInvokerAdapter invokerAdapter = new PredicateContextRepositoryInvokerAdapter(
                (PredicateContextRepository<Object, PredicateContext<?>>) repository, predicateContext, information.getInvoker());
        invokerAdapter.setAddDefaultPageable(predicateContextResolver.addDefaultPageable(entityInfo, search));
        return invokerAdapter;
    }

}
