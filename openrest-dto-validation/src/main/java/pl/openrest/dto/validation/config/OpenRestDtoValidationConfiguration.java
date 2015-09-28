package pl.openrest.dto.validation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;

import pl.openrest.dto.mapper.BeforeCreateMappingHandler;
import pl.openrest.dto.mapper.BeforeUpdateMappingHandler;
import pl.openrest.dto.mapper.MappingManager;
import pl.openrest.dto.validation.DtoFieldExpressionValidator;
import pl.openrest.dto.validation.handler.ValidationContextHandler;
import pl.openrest.dto.validation.handler.ValidatorInvoker;

@Configuration
public class OpenRestDtoValidationConfiguration {

    @Autowired
    private Validator validator;

    @Autowired(required = false)
    private DtoFieldExpressionValidator dtoFieldExpressionValidator;

    @Bean
    public ValidatorInvoker validatorInvoker() {
        return new ValidatorInvoker();
    }

    @Autowired
    public void addDtoValidatorInvokerHandler(MappingManager mappingManager) {
        ValidatorInvoker validatorInvoker = validatorInvoker();
        ValidationContextHandler contextHandler = new ValidationContextHandler();
        mappingManager.addHandler((BeforeCreateMappingHandler) contextHandler);
        mappingManager.addHandler((BeforeUpdateMappingHandler) contextHandler);
        mappingManager.addHandler((BeforeCreateMappingHandler) validatorInvoker);
        mappingManager.addHandler((BeforeUpdateMappingHandler) validatorInvoker);
    }

    @Autowired
    private void addValidators(ValidatorInvoker invoker) {
        invoker.addValidator(validator);
        if (dtoFieldExpressionValidator != null) {
            invoker.addValidator(dtoFieldExpressionValidator);
        }
    }

}
