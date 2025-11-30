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
import ua.onlinecourses.model.Student;
import ua.onlinecourses.parser.StudentFileParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class StudentTest {

    @TempDir
    Path tempDir;

    private Path testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("students.csv");
    }

    @ParameterizedTest
    @CsvSource({
            "Alina, Skrypa, alina.skrypa@chnu.edu.ua, 2023-09-01",
            "Diana, Hatsenko, diana.hatsenko@chnu.edu.ua, 2024-01-15",
            "Artem, Fivko, artem.fivko@chnu.edu.ua, 2022-10-20"
    })
    void testValidStudentCreation(String firstName, String lastName, String email, String dateStr) {
        LocalDate enrollmentDate = LocalDate.parse(dateStr);

        assertDoesNotThrow(() -> {
            Student student = new Student(firstName, lastName, email, enrollmentDate);
            assertNotNull(student);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "ThisNameIsWayTooLongToBeValidBecauseItExceedsFiftyCharactersLimit"})
    void testInvalidFirstName(String invalidName) {
        assertThrows(InvalidDataException.class, () -> {
            new Student(invalidName, "Alina", "alina.skrypa@chnu.edu.ua", LocalDate.now());
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "ThisLastNameIsWayTooLongToBeValidBecauseItExceedsFiftyCharactersLimit"})
    void testInvalidLastName(String invalidName) {
        assertThrows(InvalidDataException.class, () -> {
            new Student("Skrypa", invalidName, "alina.skrypa@chnu.edu.ua", LocalDate.now());
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "notanemail", "test@", "@example.com", "test.example.com"})
    void testInvalidEmail(String invalidEmail) {
        assertThrows(InvalidDataException.class, () -> {
            new Student("Alina", "Skrypa", invalidEmail, LocalDate.now());
        });
    }

    @Test
    void testInvalidEnrollmentDateTooOld() {
        LocalDate tooOldDate = LocalDate.now().minusYears(6);

        assertThrows(InvalidDataException.class, () -> {
            new Student("Alina", "Skrypa", "alina.skrypa@chnu.edu.ua", tooOldDate);
        });
    }

    @Test
    void testInvalidEnrollmentDateFuture() {
        LocalDate futureDate = LocalDate.now().plusYears(1);

        assertThrows(InvalidDataException.class, () -> {
            new Student("Alina", "Skrypa", "alina.skrypa@chnu.edu.ua", futureDate);
        });
    }

    @ParameterizedTest
    @MethodSource("provideFullNameTestData")
    void testGetFullName(String firstName, String lastName, String email, LocalDate date, String expectedFullName) {
        Student student = new Student(firstName, lastName, email, date);

        assertEquals(expectedFullName, student.getFullName());
    }

    private static Stream<Arguments> provideFullNameTestData() {
        LocalDate testDate = LocalDate.of(2023, 9, 1);
        return Stream.of(
                Arguments.of("Alina", "Skrypa", "alina.skrypa@chnu.edu.ua", testDate, "ALISKR-ALI-2023-09-01"),
                Arguments.of("Diana", "Hatsenko", "diana.hatsenko@chnu.edu.ua", testDate, "DIAHAT-DIA-2023-09-01"),
                Arguments.of("Artem", "Fivko", "artem.fivko@chnu.edu.ua", testDate, "ARTFIV-ART-2023-09-01")
        );
    }

    @Test
    void testGetFullNameThrowsExceptionForShortNames() {
        Student student = new Student("Al", "Skrypa", "alina.skrypa@chnu.edu.ua", LocalDate.now());

        assertThrows(InvalidDataException.class, () -> {
            student.getFullName();
        });
    }

    @ParameterizedTest
    @MethodSource("provideValidCSVTestData")
    void testParseValidCSVFile(String csvContent, int expectedSize, String firstFirstName, String firstLastName) throws IOException, URISyntaxException {
        Files.writeString(testFile, csvContent);

        List<Student> students = StudentFileParser.parseFromCSV(testFile.toString());

        assertEquals(expectedSize, students.size());
        if (expectedSize > 0) {
            assertEquals(firstFirstName, students.get(0).firstName());
            assertEquals(firstLastName, students.get(0).lastName());
        }
    }

    private static Stream<Arguments> provideValidCSVTestData() {
        return Stream.of(
                Arguments.of("Alina,Skrypa,alina.skrypa@chnu.edu.ua,2023-09-01\nDiana,Hatsenko,diana.hatsenko@chnu.edu.ua,2024-01-15", 2, "Alina", "Skrypa"),
                Arguments.of("Artem,Fivko,artem.fivko@chnu.edu.ua,2022-10-20", 1, "Artem", "Fivko"),
                Arguments.of("# Comment\nMaxym,Mishtak,max.mishtak@chnu.edu.ua,2023-05-10\n\nAnya,Shevchenko,anya.shevchenko@chnu.edu.ua,2024-02-01", 2, "Maxym", "Mishtak"),
                Arguments.of("", 0, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCSVTestData")
    void testParseInvalidCSVFile(String csvContent, int expectedValidStudents) throws IOException {
        Files.writeString(testFile, csvContent);

        assertDoesNotThrow(() -> {
            List<Student> students = StudentFileParser.parseFromCSV(testFile.toString());
            assertEquals(expectedValidStudents, students.size());
        });
    }

    private static Stream<Arguments> provideInvalidCSVTestData() {
        return Stream.of(
                Arguments.of("John,Doe,john@test.com,2023-09-01\nInvalid Line\nJane,Smith,jane@test.com,2024-01-15", 2),
                Arguments.of("John,Doe,notanemail,2023-09-01\nJane,Smith,jane@test.com,2024-01-15", 1),
                Arguments.of("John,Doe,john@test.com,2023-09-01\nJane,Smith,jane@test.com,2024-01-15", 2)
        );
    }

    @Test
    void testFileNotFound() {
        assertThrows(IOException.class, () -> {
            StudentFileParser.parseFromCSV("nonexistent.csv");
        });
    }
}