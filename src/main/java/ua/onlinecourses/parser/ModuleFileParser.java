/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ua.onlinecourses.parser;

import ua.onlinecourses.exception.InvalidDataException;
import ua.onlinecourses.model.myModule;

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

public class ModuleFileParser {
   private static final Logger logger = Logger.getLogger(ModuleFileParser.class.getName()); 
   
   public static List<myModule> parseFromCSV(String filePath) throws IOException, InvalidDataException, URISyntaxException {
        List<myModule> modules = new ArrayList<>();
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

        logger.log(Level.INFO, "Starting to parse modules from file: {0}", filePath);

        List<String> lines = Files.readAllLines(path);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            try {
                myModule module = parseModuleFromLineWithNumber(line, i + 1);
                modules.add(module);
                logger.log(Level.INFO, "Parsed module from line {0}: {1}",
                        new Object[]{i + 1, module.getFullName()});
            } catch (InvalidDataException e) {
                logger.log(Level.WARNING, "Failed to parse line {0}: {1}",
                        new Object[]{i + 1, e.getMessage()});
            }
        }

        logger.log(Level.INFO, "Successfully parsed {0} modules from file", modules.size());
        return modules;
    }
   
   private static myModule parseModuleFromLineWithNumber(String line, int lineNumber) throws InvalidDataException {
        try {
            return parseModuleFromLine(line);
        } catch (InvalidDataException e) {
            throw new InvalidDataException("Line " + lineNumber + ": " + e.getMessage(), e);
        }
    }
   
    public static myModule parseModuleFromLine(String line) throws InvalidDataException {
        String[] parts = line.split(",");
        if (parts.length != 2) {
            throw new InvalidDataException(
                    "Expected format 'title, content', got: " + line
            );
        }
            String title = parts[0].trim();
            String content = parts[1].trim();
           

            return new myModule(title, content);
        
    }
   
   
}
