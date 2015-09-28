package pl.openrest.core.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenRestConfiguration extends RepositoryRestMvcConfiguration {

    private List<OpenRestConfigurer> openRestConfigurers;

    @Override
    protected List<HandlerMethodArgumentResolver> defaultMethodArgumentResolvers() {
        // TODO Auto-generated method stub
        List<HandlerMethodArgumentResolver> resolvers = super.defaultMethodArgumentResolvers();
        List<HandlerMethodArgumentResolver> newResolvers = new ArrayList<HandlerMethodArgumentResolver>(resolvers.size() + 1);
        for (int i = 1; i < resolvers.size(); i++) {
            newResolvers.add(resolvers.get(i));
        }
        for (OpenRestConfigurer configurer : openRestConfigurers) {
            configurer.addDefaultMethodArgumentResolvers(newResolvers);
        }
        return newResolvers;
    }

    @Bean
    @Override
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = super.objectMapper();
        for (OpenRestConfigurer configurer : openRestConfigurers) {
            configurer.configureObjectMapper(mapper);
        }
        return mapper;
    }

    @Override
    @Bean
    public ObjectMapper halObjectMapper() {
        ObjectMapper mapper = super.halObjectMapper();
        for (OpenRestConfigurer configurer : openRestConfigurers) {
            configurer.configureHalObjectMapper(mapper);
        }
        return mapper;
    }
}
