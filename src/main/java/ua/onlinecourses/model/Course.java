package ua.onlinecourses.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import ua.onlinecourses.exception.InvalidDataException;
import ua.onlinecourses.util.ValidationUtils;
import ua.onlinecourses.validation.ValidStartDate;

public record Course(
        @NotBlank(message = "cannot be empty")
        @Size(min = 3, max = 100, message = "must be between 3 and 100 characters")
        String title,
        
        @NotBlank(message = "cannot be empty")
        @Size(min = 3, max = 500, message = "must be between 3 and 500 characters")
        String description,
        
        @Min(value = 1, message = "must be at least 1")
        @Max(value = 5, message = "must be at most 5")
        int credits,
        
        @NotNull(message = "cannot be null")
        @ValidStartDate
        LocalDate startDate
) implements Comparable<Course> {

    private static final Logger logger = Logger.getLogger(Course.class.getName());

    public static final Comparator<Course> BY_CREDITS =
            Comparator.comparingInt(Course::credits);

    public static final Comparator<Course> BY_START_DATE =
            Comparator.comparing(Course::startDate);

    public static final Comparator<Course> BY_TITLE =
            Comparator.comparing(Course::title)
                    .thenComparing(Course::description);

    public Course(String title, String description, int credits, LocalDate startDate) {
        this.title = title;
        this.description = description;
        this.credits = credits;
        this.startDate = startDate;
        logger.log(Level.INFO, "Attempting to create Course: {0}, {1}, {2}, {3}",
                new Object[]{title, description, credits, startDate});
        ValidationUtils.validate(this);
        logger.log(Level.INFO, "Course created successfully: {0}, {1}, {2}, {3}",
                new Object[]{title, description, credits, startDate});
    }

    static Course createCourse(String title, String description, int credits, LocalDate startDate) {
        return new Course(title, description, credits, startDate);
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getFullName() {
        if (title.length() < 3 || description.length() < 3) {
            String errorMsg = "Cannot create full name";
            throw new InvalidDataException(errorMsg);
        }
        String fullName = title.substring(0, 3).toUpperCase() + "-" +
                description.substring(0, 3).toUpperCase() + "-" +
                credits + startDate.toString();
        return fullName;
    }

    @Override
    public int compareTo(Course other) {
        return this.getFullName().compareTo(other.getFullName());
    }
}