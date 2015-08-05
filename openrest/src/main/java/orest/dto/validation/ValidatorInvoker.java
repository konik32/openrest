package orest.dto.validation;

import java.util.ArrayList;
import java.util.List;

import orest.dto.Dto;
import orest.dto.handler.DtoHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ValidatorInvoker implements DtoHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorInvoker.class);

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
		Dto dtoAnn = AnnotationUtils.findAnnotation(dto.getClass(), Dto.class);
		if (dtoAnn == null)
			return;

		Class<?> domainType = dtoAnn.entityType();
		Errors errors = new DtoValidationErrors(domainType.getSimpleName(), dto);

		for (Validator v : validators) {
			if (v.supports(domainType)) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Validating {} with {}", dto, v);
				ValidationUtils.invokeValidator(v, dto, errors);
			}
		}

		if (errors.getErrorCount() > 0) {
			throw new RepositoryConstraintViolationException(errors);
		}

	}

}
