package ua.onlinecourses.serializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.logging.Level;
import java.util.logging.Logger;

public class YamlDataSerializer<T> extends AbstractDataSerializer<T> {

    private static final Logger logger = Logger.getLogger(YamlDataSerializer.class.getName());

    public YamlDataSerializer() {
        super(createDefaultYamlObjectMapper());
        logger.log(Level.FINE, "YamlDataSerializer initialized with custom YAML configuration");
    }

    public YamlDataSerializer(ObjectMapper objectMapper) {
        super(objectMapper);
        logger.log(Level.FINE, "YamlDataSerializer initialized with custom ObjectMapper");
    }

    private static ObjectMapper createDefaultYamlObjectMapper() {
        YAMLFactory yamlFactory = new YAMLFactory()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);

        ObjectMapper mapper = new ObjectMapper(yamlFactory);
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    @Override
    public String getFormat() {
        return "YAML";
    }
}