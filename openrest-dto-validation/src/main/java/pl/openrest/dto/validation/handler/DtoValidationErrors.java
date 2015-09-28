package pl.openrest.dto.validation.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class DtoValidationErrors extends AbstractErrors {
	private static final Logger LOGGER = LoggerFactory.getLogger(DtoValidationErrors.class);

	private static final long serialVersionUID = 8141826537389141361L;

	private String name;
	private Object dto;
	private List<ObjectError> globalErrors = new ArrayList<ObjectError>();
	private List<FieldError> fieldErrors = new ArrayList<FieldError>();

	public DtoValidationErrors(String name, Object dto) {
		this.name = name;
		this.dto = dto;
	}

	@Override
	public String getObjectName() {
		return name;
	}

	@Override
	public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
		globalErrors.add(new ObjectError(name, new String[] { errorCode }, errorArgs, defaultMessage));
	}

	@Override
	public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
		fieldErrors.add(new FieldError(name, field, getFieldValue(field), true, new String[] { errorCode }, errorArgs,
				defaultMessage));
	}

	@Override
	public void addAllErrors(Errors errors) {
		globalErrors.addAll(errors.getAllErrors());
	}

	@Override
	public List<ObjectError> getGlobalErrors() {
		return globalErrors;
	}

	@Override
	public List<FieldError> getFieldErrors() {
		return fieldErrors;
	}

	@Override
	public Object getFieldValue(String field) {
		PropertyUtilsBean utils = new PropertyUtilsBean();
		try {
			return utils.getNestedProperty(dto, field);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			LOGGER.error("Cannot read property " + field + " from " + name, e);
			return null;
		}
	}

}
