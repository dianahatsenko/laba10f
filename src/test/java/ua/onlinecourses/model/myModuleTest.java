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
import ua.onlinecourses.model.myModule;
import ua.onlinecourses.parser.ModuleFileParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class myModuleTest {

    @TempDir
    Path tempDir;

    private Path testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("modules.csv");
    }

    @ParameterizedTest
    @CsvSource({
            "Introduction, Basic concepts and fundamentals",
            "Advanced Topics, Deep dive into complex subjects",
            "Java Basics, Learn Java programming"
    })
    void testValidModuleCreation(String title, String content) {
        assertDoesNotThrow(() -> {
            myModule module = new myModule(title, content);
            assertNotNull(module);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "Economics for computer science basic level introdaction for statistcs ans risks"})
    void testInvalidTitle(String invalidTitle) {
        assertThrows(InvalidDataException.class, () -> {
            new myModule(invalidTitle, "Valid content description");
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testInvalidContent(String invalidContent) {
        assertThrows(InvalidDataException.class, () -> {
            new myModule("Economics", invalidContent);
        });
    }

    @Test
    void testInvalidContentTooLong() {
        String tooLongContent = "a".repeat(2001);

        assertThrows(InvalidDataException.class, () -> {
            new myModule("Phisics", tooLongContent);
        });
    }

    @ParameterizedTest
    @MethodSource("provideFullNameTestData")
    void testGetFullName(String title, String content, String expectedFullName) {
        myModule module = new myModule(title, content);

        assertEquals(expectedFullName, module.getFullName());
    }

    private static Stream<Arguments> provideFullNameTestData() {
        return Stream.of(
                Arguments.of("Introduction", "Basic concepts", "INT-BAS"),
                Arguments.of("Advanced Math", "Complex topics", "ADV-COM"),
                Arguments.of("Java Basics", "Learning", "JAV-LEA"),
                Arguments.of("Python", "Programming", "PYT-PRO")
        );
    }

    @Test
    void testGetFullNameThrowsExceptionForShortTitle() {
        myModule module = new myModule("AB", "Content description");

        assertThrows(InvalidDataException.class, () -> {
            module.getFullName();
        });
    }

    @Test
    void testGetFullNameThrowsExceptionForShortContent() {
        myModule module = new myModule("Title", "AB");

        assertThrows(InvalidDataException.class, () -> {
            module.getFullName();
        });
    }

    @ParameterizedTest
    @MethodSource("provideValidCSVTestData")
    void testParseValidCSVFile(String csvContent, int expectedSize, String firstTitle, String firstContent) throws IOException, URISyntaxException {
        Files.writeString(testFile, csvContent);

        List<myModule> modules = ModuleFileParser.parseFromCSV(testFile.toString());

        assertEquals(expectedSize, modules.size());
        if (expectedSize > 0) {
            assertEquals(firstTitle, modules.get(0).title());
            assertEquals(firstContent, modules.get(0).content());
        }
    }

    private static Stream<Arguments> provideValidCSVTestData() {
        return Stream.of(
                Arguments.of("Introduction,Basic concepts\nAdvanced,Complex topics", 2, "Introduction", "Basic concepts"),
                Arguments.of("Java Basics,Learn Java programming", 1, "Java Basics", "Learn Java programming"),
                Arguments.of("# Comment\nAlgorithms,Study algorithms\n\nDatabases,Database design", 2, "Algorithms", "Study algorithms"),
                Arguments.of("", 0, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCSVTestData")
    void testParseInvalidCSVFile(String csvContent, int expectedValidModules) throws IOException {
        Files.writeString(testFile, csvContent);

        assertDoesNotThrow(() -> {
            List<myModule> modules = ModuleFileParser.parseFromCSV(testFile.toString());
            assertEquals(expectedValidModules, modules.size());
        });
    }

    private static Stream<Arguments> provideInvalidCSVTestData() {
        return Stream.of(
                Arguments.of("Introduction,Basic concepts\nInvalid Line\nAdvanced,Complex topics", 2),
                Arguments.of("Ukrainion History for computer science learners,Content\nAdvanced,Complex topics", 1),
                Arguments.of("Title,\nAdvanced,Complex topics", 1)
        );
    }

    @Test
    void testFileNotFound() {
        assertThrows(IOException.class, () -> {
            ModuleFileParser.parseFromCSV("nonexistent.csv");
        });
    }
}