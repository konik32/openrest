package pl.openrest.core.config;

import java.util.List;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import pl.openrest.core.webmvc.RepositoryInvokerResolver;

public interface OpenRestConfigurer {

    void addDefaultMethodArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers);

}
