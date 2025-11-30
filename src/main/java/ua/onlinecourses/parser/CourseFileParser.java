/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.onlinecourses.parser;


import ua.onlinecourses.exception.InvalidDataException;
import ua.onlinecourses.model.Course;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CourseFileParser {
   private static final Logger logger = Logger.getLogger(CourseFileParser.class.getName()); 
   
   public static List<Course> parseFromCSV(String filePath) throws IOException, InvalidDataException, URISyntaxException {
        List<Course> courses = new ArrayList<>();
       Path path;
       try {
           ClassLoader classloader = Thread.currentThread().getContextClassLoader();
           URL url = classloader.getResource(filePath);
           if (url != null) {
               path = Paths.get(url.toURI());
           } else {
               path = Paths.get(filePath);
           }
       } catch (URISyntaxException e) {
           path = Paths.get(filePath);
       }

        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }

        logger.log(Level.INFO, "Starting to parse courses from file: {0}", filePath);

        List<String> lines = Files.readAllLines(path);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            try {
                Course course = parseCourseFromLineWithNumber(line, i + 1);
                courses.add(course);
                logger.log(Level.INFO, "Parsed course from line {0}: {1}",
                        new Object[]{i + 1, course.getFullName()});
            } catch (InvalidDataException e) {
                logger.log(Level.WARNING, "Failed to parse line {0}: {1}",
                        new Object[]{i + 1, e.getMessage()});
            }
        }

        logger.log(Level.INFO, "Successfully parsed {0} courses from file", courses.size());
        return courses;
    }
   
   private static Course parseCourseFromLineWithNumber(String line, int lineNumber) throws InvalidDataException {
        try {
            return parseCourseFromLine(line);
        } catch (InvalidDataException e) {
            throw new InvalidDataException("Line " + lineNumber + ": " + e.getMessage(), e);
        }
    }
   
    public static Course parseCourseFromLine(String line) throws InvalidDataException {
        String[] parts = line.split(",");
        if (parts.length != 4) {
            throw new InvalidDataException(
                    "Expected format 'title, description, credits, startDate', got: " + line
            );
        }
            String title = parts[0].trim();
            String description = parts[1].trim();
            int credits = Integer.parseInt(parts[2].trim());
            LocalDate startDate = LocalDate.parse(parts[3].trim());
           

            return new Course(title, description, credits, startDate);
        
    }
   
   
}

