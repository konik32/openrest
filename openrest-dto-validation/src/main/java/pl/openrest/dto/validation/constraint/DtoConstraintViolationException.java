package pl.openrest.dto.validation.constraint;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.Errors;

public class DtoConstraintViolationException extends DataIntegrityViolationException {

    private static final long serialVersionUID = 3173252184193039689L;
    private final Errors errors;

    public DtoConstraintViolationException(Errors errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}
