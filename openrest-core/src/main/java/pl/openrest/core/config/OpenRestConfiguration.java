package pl.openrest.core.config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.rest.webmvc.config.RootResourceInformationHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import pl.openrest.core.webmvc.OpenRestAwareRootResourceInformationHandlerMethodArgumentResolver;
import pl.openrest.core.webmvc.RepositoryInvokerResolver;

@Configuration
public class OpenRestConfiguration extends RepositoryRestMvcConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    
    @Override
    protected List<HandlerMethodArgumentResolver> defaultMethodArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = super.defaultMethodArgumentResolvers();
        List<HandlerMethodArgumentResolver> newResolvers = new ArrayList<HandlerMethodArgumentResolver>(resolvers.size() + 1);
        newResolvers.addAll(resolvers);
        for (OpenRestConfigurer configurer : applicationContext.getBeansOfType(OpenRestConfigurer.class).values()) {
            configurer.addDefaultMethodArgumentResolvers(newResolvers);
        }
        return newResolvers;
    }

    @Override
    public RootResourceInformationHandlerMethodArgumentResolver repoRequestArgumentResolver() {
        OpenRestAwareRootResourceInformationHandlerMethodArgumentResolver resolver = new OpenRestAwareRootResourceInformationHandlerMethodArgumentResolver(repositories(), repositoryInvokerFactory(),
                resourceMetadataHandlerMethodArgumentResolver());
        List<RepositoryInvokerResolver> invokerResolvers= new LinkedList<RepositoryInvokerResolver>();
        for (OpenRestConfigurer configurer : applicationContext.getBeansOfType(OpenRestConfigurer.class).values()) {
            configurer.addRepositoryInvokerResolvers(invokerResolvers);
        }
        resolver.setRepositoryInvokerResolvers(invokerResolvers);
        return resolver;
    }

}
