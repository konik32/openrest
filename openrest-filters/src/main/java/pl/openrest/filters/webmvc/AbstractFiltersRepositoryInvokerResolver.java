package pl.openrest.filters.webmvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import pl.openrest.core.webmvc.RepositoryInvokerResolver;
import pl.openrest.filters.domain.registry.FilterableEntityRegistry;

public abstract class AbstractFiltersRepositoryInvokerResolver implements RepositoryInvokerResolver {

    protected final FilterableEntityRegistry filterableEntityRegistry;
    protected final PredicateContextResolver predicateContextResolver;

    public AbstractFiltersRepositoryInvokerResolver(FilterableEntityRegistry filterableEntityRegistry,
            PredicateContextResolver predicateContextResolver) {
        this.filterableEntityRegistry = filterableEntityRegistry;
        this.predicateContextResolver = predicateContextResolver;
    }

    @Override
    public boolean support(Class<?> domainType, NativeWebRequest webRequest) {
        return webRequest.getParameter("orest") != null && filterableEntityRegistry.get(domainType) != null;
    }

    protected static Map<String, String> extractUriTemplates(NativeWebRequest request) {
        Map<String, String> uriTemplateVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
                RequestAttributes.SCOPE_REQUEST);
        return (uriTemplateVars != null) ? uriTemplateVars : Collections.<String, String> emptyMap();
    }

    protected static MultiValueMap<String, String> toMultiValueMap(Map<String, String[]> source) {

        MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>();

        for (String key : source.keySet()) {
            result.put(key, Arrays.asList(source.get(key)));
        }

        return result;
    }

}
