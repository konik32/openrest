package pl.openrest.dto.handler.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import pl.openrest.dto.handler.BeforeCreateMappingHandler;
import pl.openrest.dto.handler.BeforeUpdateMappingHandler;
import pl.openrest.dto.validation.constraint.DtoConstraintViolationException;

public class ValidatorInvoker implements BeforeCreateMappingHandler, BeforeUpdateMappingHandler {

    private final List<Validator> validators = new ArrayList<Validator>();

    @Override
    public void handle(Object dto) {
        validate(dto);
    }

    @Override
    public void handle(Object dto, Object entity) {
        handle(dto);
    }

    public void addValidator(Validator validator) {
        validators.add(validator);
    }

    private void validate(Object dto) {
        Errors errors = new DtoValidationErrors(dto.getClass().getSimpleName(), dto);
        for (Validator v : validators) {
            if (v.supports(dto.getClass())) {
                ValidationUtils.invokeValidator(v, dto, errors);
                if (errors.getErrorCount() > 0) {
                    throw new DtoConstraintViolationException(errors);
                }
            }
        }

    }

}
