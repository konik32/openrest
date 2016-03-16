package pl.openrest.dto.config;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.config.PersistentEntityResourceHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.config.RootResourceInformationHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.support.BackendIdHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import pl.openrest.core.config.OpenRestConfigurer;
import pl.openrest.core.webmvc.RepositoryInvokerResolver;
import pl.openrest.dto.mapper.MappingManager;
import pl.openrest.dto.webmvc.DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver;
import pl.openrest.dto.webmvc.DtoRepositoryInvokerResolver;

public class OpenRestDtoConfigurer implements OpenRestConfigurer {

    @Autowired
    private DtoRepositoryInvokerResolver dtoRepositoryInvokerResolver;
    @Autowired
    private MappingManager mappingManager;

    @Override
    public void addDefaultMethodArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        RootResourceInformationHandlerMethodArgumentResolver rootResourceResolver = null;
        BackendIdHandlerMethodArgumentResolver backendIdResolver = null;
        Iterator<HandlerMethodArgumentResolver> it = resolvers.iterator();
        while (it.hasNext()) {
            HandlerMethodArgumentResolver resolver = it.next();
            if (resolver instanceof RootResourceInformationHandlerMethodArgumentResolver)
                rootResourceResolver = (RootResourceInformationHandlerMethodArgumentResolver) resolver;
            else if (resolver instanceof BackendIdHandlerMethodArgumentResolver)
                backendIdResolver = (BackendIdHandlerMethodArgumentResolver) resolver;
        }
        resolvers.add(0, new DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver(rootResourceResolver, backendIdResolver,
                mappingManager));
    }

    @Override
    public void addRepositoryInvokerResolvers(List<RepositoryInvokerResolver> resolvers) {
        resolvers.add(dtoRepositoryInvokerResolver);
    }

}
