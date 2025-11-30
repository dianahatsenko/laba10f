/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.onlinecourses.parser;

/**
 *
 * @author dinag
 */
import ua.onlinecourses.exception.InvalidDataException;
import ua.onlinecourses.model.Instructor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class InstructorFileParser {
   private static final Logger logger = Logger.getLogger(InstructorFileParser.class.getName()); 
   
   public static List<Instructor> parseFromCSV(String filePath) throws IOException, InvalidDataException, URISyntaxException {
        List<Instructor> instructors = new ArrayList<>();
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

        logger.log(Level.INFO, "Starting to parse instructors from file: {0}", filePath);

        List<String> lines = Files.readAllLines(path);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            try {
                Instructor instructor = parseInstructorFromLineWithNumber(line, i + 1);
                instructors.add(instructor);
                logger.log(Level.INFO, "Parsed instructor from line {0}: {1}",
                        new Object[]{i + 1, instructor.getFullName()});
            } catch (InvalidDataException e) {
                logger.log(Level.WARNING, "Failed to parse line {0}: {1}",
                        new Object[]{i + 1, e.getMessage()});
            }
        }

        logger.log(Level.INFO, "Successfully parsed {0} instructors from file", instructors.size());
        return instructors;
    }
   
   private static Instructor parseInstructorFromLineWithNumber(String line, int lineNumber) throws InvalidDataException {
        try {
            return parseInstructorFromLine(line);
        } catch (InvalidDataException e) {
            throw new InvalidDataException("Line " + lineNumber + ": " + e.getMessage(), e);
        }
    }
   
    public static Instructor parseInstructorFromLine(String line) throws InvalidDataException {
        String[] parts = line.split(",");
        if (parts.length != 3) {
            throw new InvalidDataException(
                    "Expected format 'firstName, lastName, expertise', got: " + line
            );
        }
            String firstName = parts[0].trim();
            String lastName = parts[1].trim();
            int expertise = Integer.parseInt(parts[2].trim());
           

            return new Instructor(firstName, lastName, expertise);
        
    }
   
   
}
