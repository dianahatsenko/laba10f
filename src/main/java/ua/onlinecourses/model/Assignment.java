package ua.onlinecourses.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import ua.onlinecourses.exception.InvalidDataException;
import ua.onlinecourses.util.ValidationUtils;
import ua.onlinecourses.validation.ValidStartDate;

public record Assignment(
        myModule module,
        
        @NotNull(message = "cannot be null")
        @ValidStartDate(message = "must be within 1 year from today")
        LocalDate dueDate,
        
        @Min(value = 1, message = "must be at least 1")
        @Max(value = 100, message = "must be at most 100")
        int maxPoints,
        
        Mark mark
) implements Comparable<Assignment> {

    private static final Logger logger = Logger.getLogger(Assignment.class.getName());
    
    public static final Comparator<Assignment> BY_DUE_DATE =
            Comparator.comparing(Assignment::dueDate);
            
    public static final Comparator<Assignment> BY_MAX_POINTS =
            Comparator.comparingInt(Assignment::maxPoints).reversed();
            
    public static final Comparator<Assignment> BY_MARK =
            Comparator.comparing(Assignment::mark);
            
    public static final Comparator<Assignment> BY_MODULE_AND_DATE =
            Comparator.comparing((Assignment a) -> a.module().getFullName())
                    .thenComparing(Assignment::dueDate);

    public Assignment(myModule module, LocalDate dueDate, int maxPoints, Mark mark) {
        this.module = module;
        this.dueDate = dueDate;
        this.maxPoints = maxPoints;
        this.mark = mark;
        logger.log(Level.INFO, "Attempting to create Assignment: {0}, {1}, {2}, {3}",
                new Object[]{module, dueDate, maxPoints, mark});
        ValidationUtils.validate(this);
        logger.log(Level.INFO, "Assignment created successfully: {0}, {1}, {2}, {3}",
                new Object[]{module, dueDate, maxPoints, mark});
    }

    static Assignment createAssignment(myModule module, LocalDate dueDate, int maxPoints, Mark mark) {
        return new Assignment(module, dueDate, maxPoints, mark);
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getMark() {
        return switch(this.mark) {
            case EXCELLENT -> "Your mark is excellent";
            case GOOD -> "Your mark is good.";
            case PASSED -> "You passed the exam";
            case SATISFACTORY -> "Your mark is satisfactory.";
            case LOW -> "Your mark is low.";
            case NOT_PASSED -> "You did not pass the exam.";
            default -> "Exam has not happened.";
        };
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getIdentity() {
        return module.getFullName() + "-" + dueDate.toString();
    }

    @Override
    public int compareTo(Assignment other) {
        return this.dueDate.compareTo(other.dueDate);
    }
}