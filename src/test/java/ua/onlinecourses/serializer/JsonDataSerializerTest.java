package ua.onlinecourses.serializer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.onlinecourses.exception.DataSerializationException;
import ua.onlinecourses.model.Student;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonDataSerializer Tests")
class JsonDataSerializerTest {

    private JsonDataSerializer<Student> serializer;
    private String testFilePath;
    private List<Student> testStudents;

    @BeforeEach
    void setUp() {
        serializer = new JsonDataSerializer<>();
        testFilePath = "./test_data/students_test.json";

        testStudents = new ArrayList<>();
        testStudents.add(new Student("Lesia", "Melnyk", "lesia.melnyk@chnu.edu.ua", LocalDate.of(2025, 1, 1)));
        testStudents.add(new Student("Liliya", "Fivko", "liliya.fivko@student.ua", LocalDate.of(2025, 1, 2)));
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
    @DisplayName("serialize should save students to JSON file")
    void testSerialize() throws DataSerializationException {
        serializer.serialize(testStudents, testFilePath);

        File file = new File(testFilePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    @DisplayName("deserialize should load students from JSON file")
    void testDeserialize() throws DataSerializationException {
        serializer.serialize(testStudents, testFilePath);

        List<Student> loaded = serializer.deserialize(testFilePath, Student.class);

        assertEquals(testStudents.size(), loaded.size());
        assertEquals(testStudents.get(0).email(), loaded.get(0).email());
        assertEquals(testStudents.get(1).email(), loaded.get(1).email());
    }

    @Test
    @DisplayName("deserialize from non-existent file should return empty list")
    void testDeserializeNonExistentFile() throws DataSerializationException {
        List<Student> loaded = serializer.deserialize("./nonexistent.json", Student.class);

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
    @DisplayName("serialize with null file path should throw exception")
    void testSerializeNullFilePath() {
        assertThrows(DataSerializationException.class, () ->
                serializer.serialize(testStudents, null)
        );
    }

    @Test
    @DisplayName("deserialize with null class should throw exception")
    void testDeserializeNullClass() {
        assertThrows(DataSerializationException.class, () ->
                serializer.deserialize(testFilePath, null)
        );
    }

    @Test
    @DisplayName("getFormat should return JSON")
    void testGetFormat() {
        assertEquals("JSON", serializer.getFormat());
    }

    @Test
    @DisplayName("serialize and deserialize should preserve data integrity")
    void testDataIntegrity() throws DataSerializationException {
        serializer.serialize(testStudents, testFilePath);
        List<Student> loaded = serializer.deserialize(testFilePath, Student.class);

        for (int i = 0; i < testStudents.size(); i++) {
            Student original = testStudents.get(i);
            Student deserialized = loaded.get(i);

            assertEquals(original.firstName(), deserialized.firstName());
            assertEquals(original.lastName(), deserialized.lastName());
            assertEquals(original.email(), deserialized.email());
            assertEquals(original.enrollmentDate(), deserialized.enrollmentDate());
        }
    }

    @Test
    @DisplayName("serialize empty list should create empty JSON array")
    void testSerializeEmptyList() throws DataSerializationException {
        List<Student> emptyList = new ArrayList<>();
        serializer.serialize(emptyList, testFilePath);

        List<Student> loaded = serializer.deserialize(testFilePath, Student.class);
        assertTrue(loaded.isEmpty());
    }
}