package ua.onlinecourses.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class EnrollmentDateValidator implements ConstraintValidator<ValidEnrollmentDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        LocalDate minDate = LocalDate.now().minusYears(5);
        return value.isAfter(minDate) && value.getYear() <= LocalDate.now().getYear();
    }
}
