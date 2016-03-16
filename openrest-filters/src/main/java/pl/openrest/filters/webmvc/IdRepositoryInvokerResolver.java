package pl.openrest.filters.webmvc;

import java.io.Serializable;

import org.springframework.core.MethodParameter;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.webmvc.BaseUri;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter.DefaultIdConverter;
import org.springframework.data.rest.webmvc.util.UriUtils;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.web.context.request.NativeWebRequest;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.domain.registry.FilterableEntityRegistry;
import pl.openrest.filters.query.PredicateContext;
import pl.openrest.filters.repository.PredicateContextRepository;
import pl.openrest.filters.repository.PredicateContextRepositoryInvokerAdapter;

public class IdRepositoryInvokerResolver extends AbstractFiltersRepositoryInvokerResolver {

    private final PluginRegistry<BackendIdConverter, Class<?>> idConverters;
    private final BaseUri baseUri;

    public IdRepositoryInvokerResolver(FilterableEntityRegistry filterableEntityRegistry,
            PredicateContextResolver predicateContextResolver, PluginRegistry<BackendIdConverter, Class<?>> idConverters, BaseUri baseUri) {
        super(filterableEntityRegistry, predicateContextResolver);
        this.baseUri = baseUri;
        this.idConverters = idConverters;
    }

    private static final String ID_URI_TEMPLATE = "id";

    @Override
    public boolean support(Class<?> domainType, NativeWebRequest webRequest) {
        if (super.support(domainType, webRequest)) {
            return extractUriTemplates(webRequest).containsKey(ID_URI_TEMPLATE);
        }
        return false;
    }

    @Override
    public RepositoryInvoker resolve(RootResourceInformation information, Object repository, MethodParameter parameter,
            NativeWebRequest webRequest) {
        FilterableEntityInformation entityInfo = filterableEntityRegistry.get(information.getDomainType());

        PersistentProperty<?> idProperty = information.getPersistentEntity().getIdProperty();
        BackendIdConverter pluginFor = idConverters.getPluginFor(information.getDomainType(), DefaultIdConverter.INSTANCE);
        String lookupPath = baseUri.getRepositoryLookupPath(webRequest);
        Serializable id = pluginFor.fromRequestId(UriUtils.findMappingVariable("id", parameter.getMethod(), lookupPath),
                information.getDomainType());

        PredicateContext<?> predicateContext = predicateContextResolver.resolve(entityInfo, toMultiValueMap(webRequest.getParameterMap()),
                idProperty, id);
        return new PredicateContextRepositoryInvokerAdapter((PredicateContextRepository<Object, PredicateContext<?>>) repository,
                predicateContext, information.getInvoker());
    }

}
