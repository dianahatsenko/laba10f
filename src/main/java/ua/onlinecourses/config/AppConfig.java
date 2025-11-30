package ua.onlinecourses.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppConfig {

    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
    private final Properties properties;
    private final String configFilePath;

    public AppConfig() {
        this("config.properties");
    }

    public AppConfig(String configFilePath) {
        this.configFilePath = configFilePath;
        this.properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        InputStream input = null;

        try {
            input = getClass().getClassLoader().getResourceAsStream(configFilePath);

            if (input == null) {
                logger.log(Level.WARNING, "Configuration file not found in classpath: {0}. Trying file system...", configFilePath);
                input = new FileInputStream(configFilePath);
            }

            properties.load(input);
            logger.log(Level.INFO, "Configuration loaded successfully from: {0}", configFilePath);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load configuration file: {0}", configFilePath);
            throw new RuntimeException("Unable to find configuration file: " + configFilePath, e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Failed to close input stream", e);
                }
            }
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public String getJsonFilePath(String entityType) {
        String basePath = getBaseDataPath();
        String key = String.format("data.path.%s.json", entityType.toLowerCase());
        String filename = getProperty(key);

        if (filename == null) {
            logger.log(Level.WARNING, "JSON filename not found for entity: {0}. Using default.", entityType);
            filename = String.format("%s.json", entityType.toLowerCase());
        }

        return combinePaths(basePath, filename);
    }

    public String getYamlFilePath(String entityType) {
        String basePath = getBaseDataPath();
        String key = String.format("data.path.%s.yaml", entityType.toLowerCase());
        String filename = getProperty(key);

        if (filename == null) {
            logger.log(Level.WARNING, "YAML filename not found for entity: {0}. Using default.", entityType);
            filename = String.format("%s.yaml", entityType.toLowerCase());
        }

        return combinePaths(basePath, filename);
    }

    public String getBaseDataPath() {
        return getProperty("data.path.base", "./data");
    }

    private String combinePaths(String basePath, String filename) {
        Path base = Paths.get(basePath);
        Path file = Paths.get(filename);

        if (file.isAbsolute()) {
            return filename;
        }

        return base.resolve(file).toString();
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid integer value for key {0}: {1}. Using default: {2}",
                    new Object[]{key, value, defaultValue});
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value);
    }

    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    public Properties getAllProperties() {
        return new Properties(properties);
    }
}