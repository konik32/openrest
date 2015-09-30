package pl.openrest.core.config;

import java.util.List;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;

public interface OpenRestConfigurer {

    public void addDefaultMethodArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers);

}
