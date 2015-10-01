package pl.openrest.dto.validation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.validation.Validator;

import pl.openrest.dto.mapper.BeforeCreateMappingHandler;
import pl.openrest.dto.mapper.BeforeUpdateMappingHandler;
import pl.openrest.dto.mapper.MappingManager;
import pl.openrest.dto.validation.DtoFieldExpressionValidator;
import pl.openrest.dto.validation.handler.ValidationContext;
import pl.openrest.dto.validation.handler.ValidationContextHandler;
import pl.openrest.dto.validation.handler.ValidatorInvoker;

@Configuration
public class OpenRestDtoValidationConfiguration {

    @Autowired
    private Validator mvcValidator;

    @Autowired(required = false)
    private DtoFieldExpressionValidator dtoFieldExpressionValidator;

    @Bean
    public ValidatorInvoker validatorInvoker() {
        return new ValidatorInvoker();
    }

    @Autowired
    public void addDtoValidatorInvokerHandler(MappingManager mappingManager, ValidationContextHandler validationContextHandler) {
        ValidatorInvoker validatorInvoker = validatorInvoker();
        mappingManager.addHandler((BeforeCreateMappingHandler) validationContextHandler);
        mappingManager.addHandler((BeforeUpdateMappingHandler) validationContextHandler);
        mappingManager.addHandler((BeforeCreateMappingHandler) validatorInvoker);
        mappingManager.addHandler((BeforeUpdateMappingHandler) validatorInvoker);
    }

    @Autowired
    public void addValidators(ValidatorInvoker invoker) {
        invoker.addValidator(mvcValidator);
        if (dtoFieldExpressionValidator != null) {
            invoker.addValidator(dtoFieldExpressionValidator);
        }
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ValidationContext validationContext() {
        return new ValidationContext();
    }

    @Bean
    public ValidationContextHandler validationContextHandler() {
        return new ValidationContextHandler();
    }

}
