package ua.onlinecourses.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AppConfig Tests")
class AppConfigTest {

    private AppConfig config;

    @BeforeEach
    void setUp() {
        config = new AppConfig();
    }

    @Test
    @DisplayName("getBaseDataPath should return configured path")
    void testGetBaseDataPath() {
        String basePath = config.getBaseDataPath();
        assertNotNull(basePath);
        assertFalse(basePath.isEmpty());
    }

    @Test
    @DisplayName("getJsonFilePath should return path for students")
    void testGetJsonFilePathStudents() {
        String path = config.getJsonFilePath("students");
        assertNotNull(path);
        assertTrue(path.contains("students"));
        assertTrue(path.endsWith(".json"));
    }

    @Test
    @DisplayName("getYamlFilePath should return path for courses")
    void testGetYamlFilePathCourses() {
        String path = config.getYamlFilePath("courses");
        assertNotNull(path);
        assertTrue(path.contains("courses"));
        assertTrue(path.endsWith(".yaml"));
    }

    @Test
    @DisplayName("getIntProperty should return integer value")
    void testGetIntProperty() {
        int count = config.getIntProperty("test.data.count", 0);
        assertTrue(count >= 0);
    }

    @Test
    @DisplayName("getIntProperty should return default for non-existent key")
    void testGetIntPropertyDefault() {
        int value = config.getIntProperty("non.existent.key", 42);
        assertEquals(42, value);
    }

    @Test
    @DisplayName("getBooleanProperty should return boolean value")
    void testGetBooleanProperty() {
        boolean value = config.getBooleanProperty("some.boolean.property", true);
        assertNotNull(value);
    }

    @Test
    @DisplayName("hasProperty should return true for existing property")
    void testHasPropertyExisting() {
        assertTrue(config.hasProperty("data.path.base") ||
                config.hasProperty("test.data.count"));
    }

    @Test
    @DisplayName("hasProperty should return false for non-existent property")
    void testHasPropertyNonExistent() {
        assertFalse(config.hasProperty("non.existent.property"));
    }

    @Test
    @DisplayName("getProperty with default should return default for non-existent key")
    void testGetPropertyWithDefault() {
        String value = config.getProperty("non.existent.key", "default");
        assertEquals("default", value);
    }

    @Test
    @DisplayName("getAllProperties should return non-null Properties")
    void testGetAllProperties() {
        assertNotNull(config.getAllProperties());
    }

    @Test
    @DisplayName("getJsonFilePath for unknown entity should use default")
    void testGetJsonFilePathUnknownEntity() {
        String path = config.getJsonFilePath("unknown_entity");
        assertNotNull(path);
        assertTrue(path.contains("unknown_entity"));
        assertTrue(path.endsWith(".json"));
    }

    @Test
    @DisplayName("getYamlFilePath for unknown entity should use default")
    void testGetYamlFilePathUnknownEntity() {
        String path = config.getYamlFilePath("unknown_entity");
        assertNotNull(path);
        assertTrue(path.contains("unknown_entity"));
        assertTrue(path.endsWith(".yaml"));
    }
}