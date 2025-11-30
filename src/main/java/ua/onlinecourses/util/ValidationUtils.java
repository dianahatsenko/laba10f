package ua.onlinecourses.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ua.onlinecourses.exception.InvalidDataException;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ValidationUtils {
    private static final Logger logger = Logger.getLogger(ValidationUtils.class.getName());
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private ValidationUtils() {
    }

    public static <T> void validate(T object) {
        logger.log(Level.INFO, "Attempting to validate object: {0}", object.getClass().getSimpleName());
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> String.format(
                            "%s: invalid value '%s' — %s",
                            v.getPropertyPath(),
                            v.getInvalidValue(),
                            v.getMessage()
                    ))
                    .collect(Collectors.joining("; "));
            logger.log(Level.WARNING, "Validation failed for {0}: {1}", 
                    new Object[]{object.getClass().getSimpleName(), errorMessage});
            throw new InvalidDataException(errorMessage);
        }
        logger.log(Level.INFO, "Validation successful for {0}", object.getClass().getSimpleName());
    }

    public static Validator getValidator() {
        return validator;
    }
}