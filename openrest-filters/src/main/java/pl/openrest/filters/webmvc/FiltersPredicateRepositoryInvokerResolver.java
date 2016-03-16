package pl.openrest.filters.webmvc;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.query.PredicateContext;
import pl.openrest.filters.repository.PredicateContextRepository;
import pl.openrest.filters.repository.PredicateContextRepositoryInvokerAdapter;

public class FiltersPredicateRepositoryInvokerResolver extends AbstractFiltersRepositoryInvokerResolver {

    public FiltersPredicateRepositoryInvokerResolver(FilterableEntityRegistry filterableEntityRegistry,
            PredicateContextResolver predicateContextResolver) {
        super(filterableEntityRegistry, predicateContextResolver);
    }

    @Override
    public RepositoryInvoker resolve(RootResourceInformation information, Object repository, MethodParameter parameter,
            NativeWebRequest webRequest) {
        FilterableEntityInformation entityInfo = filterableEntityRegistry.get(information.getDomainType());
        PredicateContext<?> predicateContext = predicateContextResolver.resolve(entityInfo, toMultiValueMap(webRequest.getParameterMap()));
        PredicateContextRepositoryInvokerAdapter invokerAdapter = new PredicateContextRepositoryInvokerAdapter(
                (PredicateContextRepository<Object, PredicateContext<?>>) repository, predicateContext, information.getInvoker());
        invokerAdapter.setAddDefaultPageable(entityInfo.getPredicateRepository().isDefaultedPageable());
        return invokerAdapter;
    }
}
