package pl.openrest.core.config;

import java.util.List;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface OpenRestConfigurer {

    public void configureObjectMapper(ObjectMapper objectMapper);

    public void configureHalObjectMapper(ObjectMapper halObjectMapper);

    public void configureRepositoryExporterHandlerMapping(RequestMappingHandlerMapping mapping);

    public void addDefaultMethodArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers);

}
