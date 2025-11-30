package ua.onlinecourses.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import ua.onlinecourses.exception.InvalidDataException;
import ua.onlinecourses.util.ValidationUtils;

public record myModule(
        @NotBlank(message = "cannot be empty")
        @Size(min = 3, max = 30, message = "must be between 3 and 30 characters")
        String title,
        
        @NotBlank(message = "cannot be empty")
        @Size(min = 3, max = 2000, message = "must be between 3 and 2000 characters")
        String content
) implements Comparable<myModule> {

    private static final Logger logger = Logger.getLogger(myModule.class.getName());
    
    public static final Comparator<myModule> BY_TITLE =
            Comparator.comparing(myModule::title);
            
    public static final Comparator<myModule> BY_CONTENT =
            Comparator.comparing(myModule::content);
            
    public static final Comparator<myModule> BY_CONTENT_LENGTH =
            Comparator.comparingInt((myModule m) -> m.content().length())
                    .thenComparing(myModule::title);

    public myModule(String title, String content) {
        this.title = title;
        this.content = content;
        logger.log(Level.INFO, "Attempting to create Module: {0}, {1}",
                new Object[]{title, content});
        ValidationUtils.validate(this);
        logger.log(Level.INFO, "Module created successfully: {0}, {1}",
                new Object[]{title, content});
    }

    static myModule createModule(String title, String content) {
        return new myModule(title, content);
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getFullName() {
        if (title.length() < 3 || content.length() < 3) {
            String errorMsg = "Cannot create full name";
            throw new InvalidDataException(errorMsg);
        }
        String fullName = title.substring(0, 3).toUpperCase() + "-" +
                content.substring(0, 3).toUpperCase();
        return fullName;
    }

    @Override
    public int compareTo(myModule other) {
        return this.getFullName().compareTo(other.getFullName());
    }
}