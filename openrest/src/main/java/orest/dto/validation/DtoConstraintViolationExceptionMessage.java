package orest.dto.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Jon Brisbin
 */
public class DtoConstraintViolationExceptionMessage {

	private final List<ValidationError> errors = new ArrayList<ValidationError>();

	public DtoConstraintViolationExceptionMessage(DtoConstraintViolationException violationException,
			MessageSourceAccessor accessor) {

		for (FieldError fieldError : violationException.getErrors().getFieldErrors()) {

			List<Object> args = new ArrayList<Object>();
			args.add(fieldError.getObjectName());
			args.add(fieldError.getField());
			args.add(fieldError.getRejectedValue());
			if (null != fieldError.getArguments()) {
				for (Object o : fieldError.getArguments()) {
					args.add(o);
				}
			}

			String message = accessor.getMessage(fieldError.getCode(), args.toArray(), fieldError.getDefaultMessage());
			this.errors.add(new ValidationError(fieldError.getObjectName(), message, String.format("%s",
					fieldError.getRejectedValue()), fieldError.getField()));
		}
	}

	@JsonProperty("errors")
	public List<ValidationError> getErrors() {
		return errors;
	}

	public static class ValidationError {

		private final String dto;
		private final String message;
		private final String invalidValue;
		private final String property;

		public ValidationError(String dto, String message, String invalidValue, String property) {
			this.dto = dto;
			this.message = message;
			this.invalidValue = invalidValue;
			this.property = property;
		}

		public String getDto() {
			return dto;
		}

		public String getMessage() {
			return message;
		}

		public String getInvalidValue() {
			return invalidValue;
		}

		public String getProperty() {
			return property;
		}
	}

}
