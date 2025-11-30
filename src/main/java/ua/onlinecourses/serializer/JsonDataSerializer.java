package ua.onlinecourses.serializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonDataSerializer<T> extends AbstractDataSerializer<T> {

    private static final Logger logger = Logger.getLogger(JsonDataSerializer.class.getName());

    public JsonDataSerializer() {
        super(createDefaultObjectMapper());
        logger.log(Level.FINE, "JsonDataSerializer initialized with pretty printing enabled");
    }

    public JsonDataSerializer(ObjectMapper objectMapper) {
        super(objectMapper);
        logger.log(Level.FINE, "JsonDataSerializer initialized with custom ObjectMapper");
    }

    private static ObjectMapper createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    @Override
    public String getFormat() {
        return "JSON";
    }
}