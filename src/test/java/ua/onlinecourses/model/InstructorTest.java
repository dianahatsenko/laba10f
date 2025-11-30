
package ua.onlinecourses.model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

import ua.onlinecourses.exception.InvalidDataException;
import ua.onlinecourses.model.Instructor;
import ua.onlinecourses.parser.InstructorFileParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class InstructorTest {

    @TempDir
    Path tempDir;

    private Path testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("instructors.csv");
    }

    @ParameterizedTest
    @CsvSource({
            "John, Doe, 10",
            "Jane, Smith, 25",
            "Alice, Brown, 5"
    })
    void testValidInstructorCreation(String firstName, String lastName, int expertise) {
        assertDoesNotThrow(() -> {
            Instructor instructor = new Instructor(firstName, lastName, expertise);
            assertNotNull(instructor);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "AlexAlexAlllleksandrErrorAaaaleksanderProblemAaalllekks"})
    void testInvalidFirstName(String invalidName) {
        assertThrows(InvalidDataException.class, () -> {
            new Instructor(invalidName, "Alexandrov", 10);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "MaksymMaksymMaksymMaksymMaksymMaksymMaksymMaksymMaksymMaksymMaksymMaksymMaksymMaksymMaksymMaksymMaksym"})
    void testInvalidLastName(String invalidName) {
        assertThrows(InvalidDataException.class, () -> {
            new Instructor("Maksym", invalidName, 10);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5, 61, 100})
    void testInvalidExpertise(int invalidExpertise) {
        assertThrows(InvalidDataException.class, () -> {
            new Instructor("Maksym", "Alexandrov", invalidExpertise);
        });
    }

    @ParameterizedTest
    @MethodSource("provideFullNameTestData")
    void testGetFullName(String firstName, String lastName, int expertise, String expectedFullName) {
        Instructor instructor = new Instructor(firstName, lastName, expertise);

        assertEquals(expectedFullName, instructor.getFullName());
    }

    private static Stream<Arguments> provideFullNameTestData() {
        return Stream.of(
                Arguments.of("Alexandrov", "Maksym", 10, "ALEMAK-10"),
                Arguments.of("Ivan", "Danulyk", 25, "IVADAN-25"),
                Arguments.of("Olga", "Marchenko", 5, "OLGMAR-5"),
                Arguments.of("Halyna", "Unguryan", 60, "HALUNG-60")
        );
    }

    @Test
    void testGetFullNameThrowsExceptionForShortFirstName() {
        Instructor instructor = new Instructor("Ol", "Marchenko", 10);

        assertThrows(InvalidDataException.class, () -> {
            instructor.getFullName();
        });
    }

    @Test
    void testGetFullNameThrowsExceptionForShortLastName() {
        Instructor instructor = new Instructor("Olga", "Ma", 10);

        assertThrows(InvalidDataException.class, () -> {
            instructor.getFullName();
        });
    }

    @ParameterizedTest
    @MethodSource("provideValidCSVTestData")
    void testParseValidCSVFile(String csvContent, int expectedSize, String firstFirstName, String firstLastName) throws IOException, URISyntaxException {
        Files.writeString(testFile, csvContent);

        List<Instructor> instructors = InstructorFileParser.parseFromCSV(testFile.toString());

        assertEquals(expectedSize, instructors.size());
        if (expectedSize > 0) {
            assertEquals(firstFirstName, instructors.get(0).firstName());
            assertEquals(firstLastName, instructors.get(0).lastName());
        }
    }

    private static Stream<Arguments> provideValidCSVTestData() {
        return Stream.of(
                Arguments.of("Alexandrov,Maksym,10\nIvan,Danulyk,25", 2, "Alexandrov", "Maksym"),
                Arguments.of("Halyna,Unguryan,5", 1, "Halyna", "Unguryan"),
                Arguments.of("# Comment\nBob,Wilson,15\n\nCarol,Davis,20", 2, "Bob", "Wilson"),
                Arguments.of("", 0, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCSVTestData")
    void testParseInvalidCSVFile(String csvContent, int expectedValidInstructors) throws IOException {
        Files.writeString(testFile, csvContent);

        assertDoesNotThrow(() -> {
            List<Instructor> instructors = InstructorFileParser.parseFromCSV(testFile.toString());
            assertEquals(expectedValidInstructors, instructors.size());
        });
    }

    private static Stream<Arguments> provideInvalidCSVTestData() {
        return Stream.of(
                Arguments.of("John,Doe,10\nInvalid Line\nJane,Smith,25", 2),
                Arguments.of("John,Doe,100\nJane,Smith,25", 1),
                Arguments.of("John,Doe,10\nJane,Smith,25", 2)
        );
    }

    @Test
    void testFileNotFound() {
        assertThrows(IOException.class, () -> {
            InstructorFileParser.parseFromCSV("nonexistent.csv");
        });
    }
}