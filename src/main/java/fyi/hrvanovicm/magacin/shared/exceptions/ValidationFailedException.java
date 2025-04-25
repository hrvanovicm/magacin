package fyi.hrvanovicm.magacin.shared.exceptions;

import java.util.Map;

public class ValidationFailedException extends RuntimeException {
    private final Map<String, String> fieldErrors;

    public ValidationFailedException(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder("Podaci nisu validni!");
        fieldErrors.forEach((field, error) -> {
            message.append("\n Greška: ").append(error);
        });
        return message.toString();
    }
}
