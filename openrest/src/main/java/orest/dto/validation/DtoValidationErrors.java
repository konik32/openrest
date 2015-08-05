package orest.dto.validation;

import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.invokeMethod;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.mysema.util.ReflectionUtils;

public class DtoValidationErrors extends AbstractErrors {

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
		Method getter = ReflectionUtils.getGetterOrNull(dto.getClass(), field);
		if (null != getter) {
			return invokeMethod(getter, dto);
		}
		Field fld = ReflectionUtils.getFieldOrNull(dto.getClass(), field);
		if (null != fld) {
			return getField(fld, dto);
		}
		return null;
	}

}
