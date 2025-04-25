package fyi.hrvanovicm.magacin.application;

import fyi.hrvanovicm.magacin.shared.exceptions.ValidationFailedException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public abstract class BaseHandler {
    @Autowired
    private Validator validator;

    public void validate(Object request) throws ValidationFailedException {
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<?> violation : violations) {
                String field = extractFieldName(violation.getPropertyPath().toString());
                String message = violation.getMessage();
                errors.put(field, message);
            }
            throw new ValidationFailedException(errors);
        }
    }

    private String extractFieldName(String path) {
        return path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
    }
}
