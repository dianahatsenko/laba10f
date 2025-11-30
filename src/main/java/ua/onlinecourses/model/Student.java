package ua.onlinecourses.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import ua.onlinecourses.exception.InvalidDataException;
import ua.onlinecourses.util.ValidationUtils;
import ua.onlinecourses.validation.ValidEnrollmentDate;

public record Student(
        @NotBlank(message = "cannot be empty")
        @Size(min = 3, max = 50, message = "must be between 3 and 50 characters")
        String firstName,
        
        @NotBlank(message = "cannot be empty")
        @Size(min = 3, max = 50, message = "must be between 3 and 50 characters")
        String lastName,
        
        @NotBlank(message = "cannot be empty")
        @Email(message = "must be a valid email address")
        String email,
        
        @NotNull(message = "cannot be null")
        @ValidEnrollmentDate
        LocalDate enrollmentDate
) implements Comparable<Student> {

    private static final Logger logger = Logger.getLogger(Student.class.getName());

    public static final Comparator<Student> BY_ENROLLMENT_DATE =
            Comparator.comparing(Student::enrollmentDate);

    public static final Comparator<Student> BY_NAME =
            Comparator.comparing(Student::lastName)
                    .thenComparing(Student::firstName)
                    .thenComparing(Student::email);

    public static final Comparator<Student> BY_NAME_DESC =
            Comparator.comparing(Student::lastName).reversed()
                    .thenComparing(Student::firstName)
                    .thenComparing(Student::email);

    public Student(String firstName, String lastName, String email, LocalDate enrollmentDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.enrollmentDate = enrollmentDate;
        logger.log(Level.INFO, "Attempting to create Student: {0}, {1}, {2}, {3}",
                new Object[]{firstName, lastName, email, enrollmentDate});
        ValidationUtils.validate(this);
        logger.log(Level.INFO, "Student created successfully: {0}, {1}, {2}, {3}",
                new Object[]{firstName, lastName, email, enrollmentDate});
    }

    static Student createStudent(String firstName, String lastName, String email, LocalDate enrollmentDate) {
        return new Student(firstName, lastName, email, enrollmentDate);
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getFullName() {
        if (firstName.length() < 3 || lastName.length() < 3 || email.length() < 3) {
            String errorMsg = "Cannot create full name";
            throw new InvalidDataException(errorMsg);
        }
        String fullName = firstName.substring(0, 3).toUpperCase() + lastName.substring(0, 3).toUpperCase() +
                "-" + email.substring(0, 3).toUpperCase() + "-" + enrollmentDate.toString();
        return fullName;
    }

    @Override
    public int compareTo(Student other) {
        return this.email.compareTo(other.email);
    }
}