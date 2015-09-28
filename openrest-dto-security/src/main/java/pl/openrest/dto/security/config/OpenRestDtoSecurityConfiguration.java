package pl.openrest.dto.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pl.openrest.dto.mapper.BeforeCreateMappingHandler;
import pl.openrest.dto.mapper.BeforeUpdateMappingHandler;
import pl.openrest.dto.mapper.MappingManager;
import pl.openrest.dto.security.authorization.AuthorizeDtoAnnotationAuthorizationStrategy;
import pl.openrest.dto.security.authorization.DtoAuthorizationStrategyMappingHandler;

@Configuration
public class OpenRestDtoSecurityConfiguration {

    @Autowired(required = false)
    private AuthorizeDtoAnnotationAuthorizationStrategy authorizeDtoAnnotationAuthorizationStrategy;

    @Bean
    public DtoAuthorizationStrategyMappingHandler dtoAuthorizationStrategyMappingHandler() {
        DtoAuthorizationStrategyMappingHandler handler = new DtoAuthorizationStrategyMappingHandler();
        if (authorizeDtoAnnotationAuthorizationStrategy != null)
            handler.addStrategy(authorizeDtoAnnotationAuthorizationStrategy);
        return handler;
    }

    @Autowired
    public void addDtoAuthorizationStrategyMappingHandler(MappingManager mappingManager) {
        mappingManager.addHandler((BeforeCreateMappingHandler) dtoAuthorizationStrategyMappingHandler());
        mappingManager.addHandler((BeforeUpdateMappingHandler) dtoAuthorizationStrategyMappingHandler());
    }

}
