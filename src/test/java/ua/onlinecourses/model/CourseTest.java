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
import ua.onlinecourses.model.Course;
import ua.onlinecourses.parser.CourseFileParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class CourseTest {

    @TempDir
    Path tempDir;

    private Path testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("courses.csv");
    }

    @ParameterizedTest
    @CsvSource({
            "Java Programming, Learn Java basics and OOP, 3, 2025-06-01",
            "Data Structures, Advanced algorithms and data structures, 4, 2025-03-15",
            "Python, Introduction to Python, 2, 2025-09-20"
    })
    void testValidCourseCreation(String title, String description, int credits, String dateStr) {
        LocalDate startDate = LocalDate.parse(dateStr);

        assertDoesNotThrow(() -> {
            Course course = new Course(title, description, credits, startDate);
            assertNotNull(course);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "Economics for computer science students analytics and learners of applied math and data structure First level include Math basics and risks So it will be great for future IT workers or programing workers"})
    void testInvalidTitle(String invalidTitle) {
        assertThrows(InvalidDataException.class, () -> {
            new Course(invalidTitle, "Economics for computer science", 3, LocalDate.now().plusMonths(1));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testInvalidDescription(String invalidDescription) {
        assertThrows(InvalidDataException.class, () -> {
            new Course("Graphs programming", invalidDescription, 3, LocalDate.now().plusMonths(1));
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 6, 10})
    void testInvalidCredits(int invalidCredits) {
        assertThrows(InvalidDataException.class, () -> {
            new Course("Statistics", "Intro to statistics and math", invalidCredits, LocalDate.now().plusMonths(1));
        });
    }

    @Test
    void testInvalidStartDateTooOld() {
        LocalDate tooOldDate = LocalDate.now().minusYears(2);

        assertThrows(InvalidDataException.class, () -> {
            new Course("Valid Title", "Valid description", 3, tooOldDate);
        });
    }

    @Test
    void testInvalidStartDateTooFar() {
        LocalDate tooFarDate = LocalDate.now().plusYears(2);

        assertThrows(InvalidDataException.class, () -> {
            new Course("PI", "Physical education", 3, tooFarDate);
        });
    }

    @ParameterizedTest
    @MethodSource("provideFullNameTestData")
    void testGetFullName(String title, String description, int credits, LocalDate startDate, String expectedFullName) {
        Course course = new Course(title, description, credits, startDate);

        assertEquals(expectedFullName, course.getFullName());
    }

    private static Stream<Arguments> provideFullNameTestData() {
        return Stream.of(
                Arguments.of("Java Programming", "Learn Java basics", 3, LocalDate.parse("2025-06-01"), "JAV-LEA-32025-06-01"),
                Arguments.of("Data Structures", "Advanced algorithms", 4, LocalDate.parse("2025-03-15"), "DAT-ADV-42025-03-15"),
                Arguments.of("Python", "Introduction", 2, LocalDate.parse("2025-09-20"), "PYT-INT-22025-09-20")
        );
    }

    @Test
    void testGetFullNameThrowsExceptionForShortTitle() {
        Course course = new Course("CS", "Computer Science basics", 3, LocalDate.now().plusMonths(1));

        assertThrows(InvalidDataException.class, () -> {
            course.getFullName();
        });
    }

    @Test
    void testGetFullNameThrowsExceptionForShortDescription() {
        Course course = new Course("Computer Science", "CS", 3, LocalDate.now().plusMonths(1));

        assertThrows(InvalidDataException.class, () -> {
            course.getFullName();
        });
    }

    @ParameterizedTest
    @MethodSource("provideValidCSVTestData")
    void testParseValidCSVFile(String csvContent, int expectedSize, String firstTitle, String firstDescription) throws IOException, URISyntaxException {
        Files.writeString(testFile, csvContent);

        List<Course> courses = CourseFileParser.parseFromCSV(testFile.toString());

        assertEquals(expectedSize, courses.size());
        if (expectedSize > 0) {
            assertEquals(firstTitle, courses.get(0).title());
            assertEquals(firstDescription, courses.get(0).description());
        }
    }

    private static Stream<Arguments> provideValidCSVTestData() {
        return Stream.of(
                Arguments.of("Java Programming,Learn Java basics,3,2025-06-01\nPython,Introduction to Python,2,2025-03-15", 2, "Java Programming", "Learn Java basics"),
                Arguments.of("Data Structures,Advanced algorithms,4,2025-03-15", 1, "Data Structures", "Advanced algorithms"),
                Arguments.of("# Comment\nAlgorithms,Study of algorithms,5,2025-05-10\n\nDatabases,Database management,3,2025-02-01", 2, "Algorithms", "Study of algorithms"),
                Arguments.of("", 0, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCSVTestData")
    void testParseInvalidCSVFile(String csvContent, int expectedValidCourses) throws IOException {
        Files.writeString(testFile, csvContent);

        assertDoesNotThrow(() -> {
            List<Course> courses = CourseFileParser.parseFromCSV(testFile.toString());
            assertEquals(expectedValidCourses, courses.size());
        });
    }

    private static Stream<Arguments> provideInvalidCSVTestData() {
        return Stream.of(
                Arguments.of("Java Programming,Learn Java,3,2025-06-01\nInvalid Line\nPython,Intro,2,2025-03-15", 2),
                Arguments.of("Java,Description,10,2025-06-01\nPython,Intro,2,2025-03-15", 1),
                Arguments.of("Java,Description,3,2025-06-01\nPython,Intro,2,2025-03-15", 2)
        );
    }

    @Test
    void testFileNotFound() {
        assertThrows(IOException.class, () -> {
            CourseFileParser.parseFromCSV("nonexistent.csv");
        });
    }
}