package ua.onlinecourses.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class StartDateValidator implements ConstraintValidator<ValidStartDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        LocalDate minDate = LocalDate.now().minusYears(1);
        LocalDate maxDate = LocalDate.now().plusYears(1);
        return value.isAfter(minDate) && value.isBefore(maxDate);
    }
}
