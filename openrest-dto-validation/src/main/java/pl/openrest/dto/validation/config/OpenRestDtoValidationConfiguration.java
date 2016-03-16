package pl.openrest.dto.validation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;

import pl.openrest.dto.handler.BeforeCreateMappingHandler;
import pl.openrest.dto.handler.BeforeUpdateMappingHandler;
import pl.openrest.dto.handler.validation.ValidatorInvoker;
import pl.openrest.dto.mapper.MappingManager;
import pl.openrest.dto.validation.DtoFieldExpressionValidator;

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
    public void addDtoValidatorInvokerHandler(MappingManager mappingManager) {
        ValidatorInvoker validatorInvoker = validatorInvoker();
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

}
