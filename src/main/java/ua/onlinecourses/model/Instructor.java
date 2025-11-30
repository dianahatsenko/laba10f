package ua.onlinecourses.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import ua.onlinecourses.exception.InvalidDataException;
import ua.onlinecourses.util.ValidationUtils;

public record Instructor(
        @NotBlank(message = "cannot be empty")
        @Size(min = 3, max = 50, message = "must be between 3 and 50 characters")
        String firstName,
        
        @NotBlank(message = "cannot be empty")
        @Size(min = 3, max = 50, message = "must be between 3 and 50 characters")
        String lastName,
        
        @Min(value = 1, message = "must be at least 1")
        @Max(value = 60, message = "must be at most 60")
        int expertise
) implements Comparable<Instructor> {

    private static final Logger logger = Logger.getLogger(Instructor.class.getName());

    public static final Comparator<Instructor> BY_EXPERTISE =
            Comparator.comparingInt(Instructor::expertise).reversed();

    public static final Comparator<Instructor> BY_LAST_NAME =
            Comparator.comparing(Instructor::lastName)
                    .thenComparing(Instructor::firstName);

    public static final Comparator<Instructor> BY_FIRST_NAME =
            Comparator.comparing(Instructor::firstName)
                    .thenComparing(Instructor::lastName);

    public Instructor(String firstName, String lastName, int expertise) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.expertise = expertise;
        logger.log(Level.INFO, "Attempting to create Instructor: {0}, {1}, {2}",
                new Object[]{firstName, lastName, expertise});
        ValidationUtils.validate(this);
        logger.log(Level.INFO, "Instructor created successfully: {0}, {1}, {2}",
                new Object[]{firstName, lastName, expertise});
    }

    static Instructor createInstructor(String firstName, String lastName, int expertise) {
        return new Instructor(firstName, lastName, expertise);
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getFullName() {
        if (firstName.length() < 3 || lastName.length() < 3) {
            String errorMsg = "Cannot create full name";
            throw new InvalidDataException(errorMsg);
        }
        String fullName = firstName.substring(0, 3).toUpperCase() +
                lastName.substring(0, 3).toUpperCase() + "-" + expertise;
        return fullName;
    }

    @Override
    public int compareTo(Instructor other) {
        return this.getFullName().compareTo(other.getFullName());
    }
}