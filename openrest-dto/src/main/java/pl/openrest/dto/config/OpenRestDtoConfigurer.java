package pl.openrest.dto.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.config.RootResourceInformationHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.support.BackendIdHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import pl.openrest.core.config.OpenRestConfigurer;
import pl.openrest.dto.mapper.MappingManager;
import pl.openrest.dto.webmvc.DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver;

public class OpenRestDtoConfigurer implements OpenRestConfigurer {

    @Autowired
    private MappingManager mappingManager;

    @Override
    public void addDefaultMethodArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers,
            RootResourceInformationHandlerMethodArgumentResolver rootResourceResolver,
            BackendIdHandlerMethodArgumentResolver backendIdResolver, ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver) {
        // TODO Auto-generated method stub
        resolvers.add(new DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver(rootResourceResolver, backendIdResolver,
                mappingManager));
    }

}
