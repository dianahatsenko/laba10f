package ua.onlinecourses.serializer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.onlinecourses.exception.DataSerializationException;
import ua.onlinecourses.model.Course;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YamlDataSerializer Tests")
class YamlDataSerializerTest {

    private YamlDataSerializer<Course> serializer;
    private String testFilePath;
    private List<Course> testCourses;

    @BeforeEach
    void setUp() {
        serializer = new YamlDataSerializer<>();
        testFilePath = "./test_data/courses_test.yaml";

        testCourses = new ArrayList<>();
        testCourses.add(new Course("Java", "Programming", 5, LocalDate.of(2025, 1, 1)));
        testCourses.add(new Course("Python", "Scripting", 4, LocalDate.of(2025, 2, 1)));
    }

    @AfterEach
    void tearDown() {
        File file = new File(testFilePath);
        if (file.exists()) {
            file.delete();
        }
        File dir = new File("./test_data");
        if (dir.exists()) {
            dir.delete();
        }
    }

    @Test
    @DisplayName("serialize should save courses to YAML file")
    void testSerialize() throws DataSerializationException {
        serializer.serialize(testCourses, testFilePath);

        File file = new File(testFilePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    @DisplayName("deserialize should load courses from YAML file")
    void testDeserialize() throws DataSerializationException {
        serializer.serialize(testCourses, testFilePath);

        List<Course> loaded = serializer.deserialize(testFilePath, Course.class);

        assertEquals(testCourses.size(), loaded.size());
        assertEquals(testCourses.get(0).title(), loaded.get(0).title());
        assertEquals(testCourses.get(1).title(), loaded.get(1).title());
    }

    @Test
    @DisplayName("deserialize from non-existent file should return empty list")
    void testDeserializeNonExistentFile() throws DataSerializationException {
        List<Course> loaded = serializer.deserialize("./nonexistent.yaml", Course.class);

        assertNotNull(loaded);
        assertTrue(loaded.isEmpty());
    }

    @Test
    @DisplayName("serialize null list should throw exception")
    void testSerializeNullList() {
        assertThrows(DataSerializationException.class, () ->
                serializer.serialize(null, testFilePath)
        );
    }

    @Test
    @DisplayName("getFormat should return YAML")
    void testGetFormat() {
        assertEquals("YAML", serializer.getFormat());
    }

    @Test
    @DisplayName("serialize and deserialize should preserve data integrity")
    void testDataIntegrity() throws DataSerializationException {
        serializer.serialize(testCourses, testFilePath);
        List<Course> loaded = serializer.deserialize(testFilePath, Course.class);

        for (int i = 0; i < testCourses.size(); i++) {
            Course original = testCourses.get(i);
            Course deserialized = loaded.get(i);

            assertEquals(original.title(), deserialized.title());
            assertEquals(original.description(), deserialized.description());
            assertEquals(original.credits(), deserialized.credits());
            assertEquals(original.startDate(), deserialized.startDate());
        }
    }

    @Test
    @DisplayName("serialize empty list should create empty YAML array")
    void testSerializeEmptyList() throws DataSerializationException {
        List<Course> emptyList = new ArrayList<>();
        serializer.serialize(emptyList, testFilePath);

        List<Course> loaded = serializer.deserialize(testFilePath, Course.class);
        assertTrue(loaded.isEmpty());
    }
}