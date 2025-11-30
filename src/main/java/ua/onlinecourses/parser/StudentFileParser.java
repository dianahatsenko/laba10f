/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.onlinecourses.parser;

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
import ua.onlinecourses.exception.InvalidDataException;
import ua.onlinecourses.model.Student;


public class StudentFileParser {
    private static final Logger logger = Logger.getLogger(StudentFileParser.class.getName()); 
   
   public static List<Student> parseFromCSV(String filePath) throws IOException, InvalidDataException, URISyntaxException {
        List<Student> students = new ArrayList<>();
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

        logger.log(Level.INFO, "Starting to parse students from file: {0}", filePath);

        List<String> lines = Files.readAllLines(path);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            try {
                Student student = parseStudentFromLineWithNumber(line, i + 1);
                students.add(student);
                logger.log(Level.INFO, "Parsed student from line {0}: {1}",
                        new Object[]{i + 1, student.getFullName()});
            } catch (InvalidDataException e) {
                logger.log(Level.WARNING, "Failed to parse line {0}: {1}",
                        new Object[]{i + 1, e.getMessage()});
            }
        }

        logger.log(Level.INFO, "Successfully parsed {0} students from file", students.size());
        return students;
    }
   
   private static Student parseStudentFromLineWithNumber(String line, int lineNumber) throws InvalidDataException {
        try {
            return parseStudentFromLine(line);
        } catch (InvalidDataException e) {
            throw new InvalidDataException("Line " + lineNumber + ": " + e.getMessage(), e);
        }
    }
   
    public static Student parseStudentFromLine(String line) throws InvalidDataException {
        String[] parts = line.split(",");
        if (parts.length != 4) {
            throw new InvalidDataException(
                    "Expected format 'firstName, lastName, email, enrollmentDate', got: " + line
            );
        }
            String firstName = parts[0].trim();
            String lastName = parts[1].trim();
            String email = parts[2].trim();
            LocalDate enrollmentDate = LocalDate.parse(parts[3].trim());
           

            return new Student(firstName, lastName, email, enrollmentDate);
        
    }
   
   
}
