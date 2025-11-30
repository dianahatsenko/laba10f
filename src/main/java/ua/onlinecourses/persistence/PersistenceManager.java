package ua.onlinecourses.persistence;

import ua.onlinecourses.config.AppConfig;
import ua.onlinecourses.exception.DataSerializationException;
import ua.onlinecourses.serializer.DataSerializer;
import ua.onlinecourses.serializer.JsonDataSerializer;
import ua.onlinecourses.serializer.YamlDataSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersistenceManager {

    private static final Logger logger = Logger.getLogger(PersistenceManager.class.getName());

    private final AppConfig config;
    private final Map<String, DataSerializer<?>> serializers;

    public PersistenceManager(AppConfig config) {
        this.config = config;
        this.serializers = new HashMap<>();
        initializeSerializers();
        logger.log(Level.INFO, "PersistenceManager initialized with {0} serializers", serializers.size());
    }

    private void initializeSerializers() {
        serializers.put("JSON", new JsonDataSerializer<>());
        serializers.put("YAML", new YamlDataSerializer<>());
        logger.log(Level.FINE, "Registered serializers: {0}", serializers.keySet());
    }

    public <T> void save(List<T> items, String entityType, Class<T> clazz, String format)
            throws DataSerializationException {
        validateParameters(items, entityType, clazz);

        String formatUpper = format.toUpperCase();
        DataSerializer<T> serializer = getSerializer(formatUpper);
        String filePath = getFilePath(entityType, formatUpper);

        logger.log(Level.INFO, "Saving {0} items of type {1} to {2} file: {3}",
                new Object[]{items.size(), entityType, formatUpper, filePath});

        try {
            serializer.serialize(items, filePath);
            logger.log(Level.INFO, "Successfully saved {0} {1} items to {2}",
                    new Object[]{items.size(), entityType, formatUpper});
        } catch (DataSerializationException e) {
            logger.log(Level.SEVERE, "Failed to save {0} to {1}: {2}",
                    new Object[]{entityType, formatUpper, e.getMessage()});
            throw e;
        }
    }

    public <T> List<T> load(String entityType, Class<T> clazz, String format)
            throws DataSerializationException {
        if (entityType == null || entityType.trim().isEmpty()) {
            throw new DataSerializationException("Entity type cannot be null or empty");
        }

        if (clazz == null) {
            throw new DataSerializationException("Class type cannot be null");
        }

        String formatUpper = format.toUpperCase();
        DataSerializer<T> serializer = getSerializer(formatUpper);
        String filePath = getFilePath(entityType, formatUpper);

        logger.log(Level.INFO, "Loading {0} from {1} file: {2}",
                new Object[]{entityType, formatUpper, filePath});

        try {
            List<T> items = serializer.deserialize(filePath, clazz);
            logger.log(Level.INFO, "Successfully loaded {0} items of type {1}",
                    new Object[]{items.size(), entityType});
            return items;
        } catch (DataSerializationException e) {
            logger.log(Level.SEVERE, "Failed to load {0} from {1}: {2}",
                    new Object[]{entityType, formatUpper, e.getMessage()});
            throw e;
        }
    }

    public <T> void saveAllFormats(List<T> items, String entityType, Class<T> clazz)
            throws DataSerializationException {
        logger.log(Level.INFO, "Saving {0} items of type {1} to all formats",
                new Object[]{items.size(), entityType});

        save(items, entityType, clazz, "JSON");
        save(items, entityType, clazz, "YAML");

        logger.log(Level.INFO, "Successfully saved {0} to all formats", entityType);
    }

    private <T> void validateParameters(List<T> items, String entityType, Class<T> clazz)
            throws DataSerializationException {
        if (items == null) {
            throw new DataSerializationException("Items list cannot be null");
        }

        if (entityType == null || entityType.trim().isEmpty()) {
            throw new DataSerializationException("Entity type cannot be null or empty");
        }

        if (clazz == null) {
            throw new DataSerializationException("Class type cannot be null");
        }
    }

    @SuppressWarnings("unchecked")
    private <T> DataSerializer<T> getSerializer(String format) throws DataSerializationException {
        DataSerializer<?> serializer = serializers.get(format);

        if (serializer == null) {
            String errorMsg = String.format("Unsupported format: %s. Available formats: %s",
                    format, serializers.keySet());
            logger.log(Level.SEVERE, errorMsg);
            throw new DataSerializationException(errorMsg);
        }

        return (DataSerializer<T>) serializer;
    }

    private String getFilePath(String entityType, String format) {
        return switch (format) {
            case "JSON" -> config.getJsonFilePath(entityType);
            case "YAML" -> config.getYamlFilePath(entityType);
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }

    public boolean isFormatSupported(String format) {
        return serializers.containsKey(format.toUpperCase());
    }

    public String[] getSupportedFormats() {
        return serializers.keySet().toArray(new String[0]);
    }
}