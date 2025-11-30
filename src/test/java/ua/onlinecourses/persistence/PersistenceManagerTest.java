package ua.onlinecourses.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.onlinecourses.config.AppConfig;
import ua.onlinecourses.exception.DataSerializationException;
import ua.onlinecourses.model.Instructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PersistenceManager Tests")
class PersistenceManagerTest {

    private PersistenceManager manager;
    private AppConfig config;
    private List<Instructor> testInstructors;

    @BeforeEach
    void setUp() {
        config = new AppConfig();
        manager = new PersistenceManager(config);

        testInstructors = new ArrayList<>();
        testInstructors.add(new Instructor("Igor", "Bylat", 18));
        testInstructors.add(new Instructor("Denys", "Skrypa", 22));
    }

    @AfterEach
    void tearDown() {
        cleanupTestFiles();
    }

    @Test
    @DisplayName("save should save data in JSON format")
    void testSaveJson() throws DataSerializationException {
        manager.save(testInstructors, "instructors", Instructor.class, "JSON");

        File file = new File(config.getJsonFilePath("instructors"));
        assertTrue(file.exists());
    }

    @Test
    @DisplayName("save should save data in YAML format")
    void testSaveYaml() throws DataSerializationException {
        manager.save(testInstructors, "instructors", Instructor.class, "YAML");

        File file = new File(config.getYamlFilePath("instructors"));
        assertTrue(file.exists());
    }

    @Test
    @DisplayName("load should load data from JSON format")
    void testLoadJson() throws DataSerializationException {
        manager.save(testInstructors, "instructors", Instructor.class, "JSON");

        List<Instructor> loaded = manager.load("instructors", Instructor.class, "JSON");

        assertEquals(testInstructors.size(), loaded.size());
    }

    @Test
    @DisplayName("load should load data from YAML format")
    void testLoadYaml() throws DataSerializationException {
        manager.save(testInstructors, "instructors", Instructor.class, "YAML");

        List<Instructor> loaded = manager.load("instructors", Instructor.class, "YAML");

        assertEquals(testInstructors.size(), loaded.size());
    }

    @Test
    @DisplayName("saveAllFormats should save data in both formats")
    void testSaveAllFormats() throws DataSerializationException {
        manager.saveAllFormats(testInstructors, "instructors", Instructor.class);

        File jsonFile = new File(config.getJsonFilePath("instructors"));
        File yamlFile = new File(config.getYamlFilePath("instructors"));

        assertTrue(jsonFile.exists());
        assertTrue(yamlFile.exists());
    }

    @Test
    @DisplayName("save with null items should throw exception")
    void testSaveNullItems() {
        assertThrows(DataSerializationException.class, () ->
                manager.save(null, "instructors", Instructor.class, "JSON")
        );
    }

    @Test
    @DisplayName("save with null entity type should throw exception")
    void testSaveNullEntityType() {
        assertThrows(DataSerializationException.class, () ->
                manager.save(testInstructors, null, Instructor.class, "JSON")
        );
    }

    @Test
    @DisplayName("save with unsupported format should throw exception")
    void testSaveUnsupportedFormat() {
        assertThrows(DataSerializationException.class, () ->
                manager.save(testInstructors, "instructors", Instructor.class, "XML")
        );
    }

    @Test
    @DisplayName("isFormatSupported should return true for JSON")
    void testIsFormatSupportedJson() {
        assertTrue(manager.isFormatSupported("JSON"));
    }

    @Test
    @DisplayName("isFormatSupported should return true for YAML")
    void testIsFormatSupportedYaml() {
        assertTrue(manager.isFormatSupported("YAML"));
    }

    @Test
    @DisplayName("isFormatSupported should return false for XML")
    void testIsFormatSupportedXml() {
        assertFalse(manager.isFormatSupported("XML"));
    }

    @Test
    @DisplayName("getSupportedFormats should return array with JSON and YAML")
    void testGetSupportedFormats() {
        String[] formats = manager.getSupportedFormats();

        assertEquals(2, formats.length);
        assertTrue(List.of(formats).contains("JSON"));
        assertTrue(List.of(formats).contains("YAML"));
    }

    @Test
    @DisplayName("load and save should preserve data")
    void testDataPreservation() throws DataSerializationException {
        manager.save(testInstructors, "instructors", Instructor.class, "JSON");
        List<Instructor> loaded = manager.load("instructors", Instructor.class, "JSON");

        assertEquals(testInstructors.size(), loaded.size());
        for (int i = 0; i < testInstructors.size(); i++) {
            assertEquals(testInstructors.get(i).firstName(), loaded.get(i).firstName());
            assertEquals(testInstructors.get(i).lastName(), loaded.get(i).lastName());
            assertEquals(testInstructors.get(i).expertise(), loaded.get(i).expertise());
        }
    }

    private void cleanupTestFiles() {
        String[] entities = {"instructors", "students", "courses"};
        for (String entity : entities) {
            File jsonFile = new File(config.getJsonFilePath(entity));
            if (jsonFile.exists()) {
                jsonFile.delete();
            }
            File yamlFile = new File(config.getYamlFilePath(entity));
            if (yamlFile.exists()) {
                yamlFile.delete();
            }
        }

        File dataDir = new File(config.getBaseDataPath());
        if (dataDir.exists() && dataDir.isDirectory()) {
            dataDir.delete();
        }
    }
}