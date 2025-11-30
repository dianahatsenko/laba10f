package ua.onlinecourses.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ua.onlinecourses.exception.InvalidDataException;
import ua.onlinecourses.model.*;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validation Tests")
public class ValidationTest {

    @Test
    @DisplayName("Valid student creation should succeed")
    void testValidStudentCreation() {
        assertDoesNotThrow(() -> {
            Student student = new Student("Artem", "Fivko", "artem.fivko@chnu.edu.ua", LocalDate.now());
            assertNotNull(student);
            assertEquals("Artem", student.firstName());
            assertEquals("Fivko", student.lastName());
            assertEquals("artem.fivko@chnu.edu.ua", student.email());
        });
    }

    @Test
    @DisplayName("Invalid student should throw InvalidDataException with all error messages")
    void testInvalidStudentWithMultipleErrors() {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            new Student("", "", "invalid-email", LocalDate.now().minusYears(10));
        });
        
        String message = exception.getMessage();
        assertTrue(message.contains("firstName"));
        assertTrue(message.contains("lastName"));
        assertTrue(message.contains("email"));
        assertTrue(message.contains("enrollmentDate"));
    }

    @Test
    @DisplayName("Valid course creation should succeed")
    void testValidCourseCreation() {
        assertDoesNotThrow(() -> {
            Course course = new Course("Java Programming", "Learn Java basics", 5, LocalDate.now().plusMonths(1));
            assertNotNull(course);
            assertEquals("Java Programming", course.title());
            assertEquals(5, course.credits());
        });
    }

    @Test
    @DisplayName("Invalid course should throw InvalidDataException with all error messages")
    void testInvalidCourseWithMultipleErrors() {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            new Course("", "", 0, LocalDate.now().plusYears(2));
        });
        
        String message = exception.getMessage();
        assertTrue(message.contains("title"));
        assertTrue(message.contains("description"));
        assertTrue(message.contains("credits"));
        assertTrue(message.contains("startDate"));
    }

    @Test
    @DisplayName("Valid instructor creation should succeed")
    void testValidInstructorCreation() {
        assertDoesNotThrow(() -> {
            Instructor instructor = new Instructor("Nataliia", "Moroz", 30);
            assertNotNull(instructor);
            assertEquals("Nataliia", instructor.firstName());
            assertEquals("Moroz", instructor.lastName());
            assertEquals(30, instructor.expertise());
        });
    }

    @Test
    @DisplayName("Invalid instructor should throw InvalidDataException with all error messages")
    void testInvalidInstructorWithMultipleErrors() {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            new Instructor("", "", 0);
        });
        
        String message = exception.getMessage();
        assertTrue(message.contains("firstName"));
        assertTrue(message.contains("lastName"));
        assertTrue(message.contains("expertise"));
    }

    @Test
    @DisplayName("Valid module creation should succeed")
    void testValidModuleCreation() {
        assertDoesNotThrow(() -> {
            myModule module = new myModule("Introduction", "Basic concepts");
            assertNotNull(module);
            assertEquals("Introduction", module.title());
            assertEquals("Basic concepts", module.content());
        });
    }

    @Test
    @DisplayName("Invalid module should throw InvalidDataException with all error messages")
    void testInvalidModuleWithMultipleErrors() {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            new myModule("", "");
        });
        
        String message = exception.getMessage();
        assertTrue(message.contains("title"));
        assertTrue(message.contains("content"));
    }

    @Test
    @DisplayName("Valid assignment creation should succeed")
    void testValidAssignmentCreation() {
        myModule module = new myModule("Test Module", "Test content");
        assertDoesNotThrow(() -> {
            Assignment assignment = new Assignment(module, LocalDate.now().plusDays(7), 100, Mark.EXCELLENT);
            assertNotNull(assignment);
            assertEquals(100, assignment.maxPoints());
        });
    }

    @Test
    @DisplayName("Invalid assignment should throw InvalidDataException with all error messages")
    void testInvalidAssignmentWithMultipleErrors() {
        myModule module = new myModule("Test Module", "Test content");
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            new Assignment(module, LocalDate.now().plusYears(2), 0, Mark.EXCELLENT);
        });
        
        String message = exception.getMessage();
        assertTrue(message.contains("dueDate") || message.contains("maxPoints"));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidStudentData")
    @DisplayName("Student validation should reject invalid data")
    void testStudentValidationRejectsInvalidData(String firstName, String lastName, String email, LocalDate enrollmentDate) {
        assertThrows(InvalidDataException.class, () -> {
            new Student(firstName, lastName, email, enrollmentDate);
        });
    }

    private static Stream<Arguments> provideInvalidStudentData() {
        return Stream.of(
                Arguments.of("", "Fivko", "artem.fivko@chnu.edu.ua", LocalDate.now()),
                Arguments.of("Artem", "", "artem.fivko@chnu.edu.ua", LocalDate.now()),
                Arguments.of("Artem", "Fivko", "invalid-email", LocalDate.now()),
                Arguments.of("Artem", "Fivko", "artem.fivko@chnu.edu.ua", LocalDate.now().minusYears(10)),
                Arguments.of("Artem", "Fivko", "artem.fivko@chnu.edu.ua", LocalDate.now().plusYears(2)),
                Arguments.of("  ", "Fivko", "artem.fivko@chnu.edu.ua", LocalDate.now()),
                Arguments.of("A".repeat(51), "Fivko", "artem.fivko@chnu.edu.ua", LocalDate.now())
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCourseData")
    @DisplayName("Course validation should reject invalid data")
    void testCourseValidationRejectsInvalidData(String title, String description, int credits, LocalDate startDate) {
        assertThrows(InvalidDataException.class, () -> {
            new Course(title, description, credits, startDate);
        });
    }

    private static Stream<Arguments> provideInvalidCourseData() {
        return Stream.of(
                Arguments.of("", "Description", 3, LocalDate.now().plusMonths(1)),
                Arguments.of("Title", "", 3, LocalDate.now().plusMonths(1)),
                Arguments.of("Title", "Description", 0, LocalDate.now().plusMonths(1)),
                Arguments.of("Title", "Description", 6, LocalDate.now().plusMonths(1)),
                Arguments.of("Title", "Description", 3, LocalDate.now().minusYears(2)),
                Arguments.of("Title", "Description", 3, LocalDate.now().plusYears(2)),
                Arguments.of("A".repeat(101), "Description", 3, LocalDate.now().plusMonths(1))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidInstructorData")
    @DisplayName("Instructor validation should reject invalid data")
    void testInstructorValidationRejectsInvalidData(String firstName, String lastName, int expertise) {
        assertThrows(InvalidDataException.class, () -> {
            new Instructor(firstName, lastName, expertise);
        });
    }

    private static Stream<Arguments> provideInvalidInstructorData() {
        return Stream.of(
                Arguments.of("", "Moroz", 15),
                Arguments.of("Nataliia", "", 15),
                Arguments.of("Nataliia", "Moroz", 0),
                Arguments.of("Nataliia", "Moroz", 61),
                Arguments.of("Nataliia", "Moroz", -5),
                Arguments.of("A".repeat(51), "Moroz", 15)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidModuleData")
    @DisplayName("Module validation should reject invalid data")
    void testModuleValidationRejectsInvalidData(String title, String content) {
        assertThrows(InvalidDataException.class, () -> {
            new myModule(title, content);
        });
    }

    private static Stream<Arguments> provideInvalidModuleData() {
        return Stream.of(
                Arguments.of("", "Content"),
                Arguments.of("Title", ""),
                Arguments.of("  ", "Content"),
                Arguments.of("Title", "   "),
                Arguments.of("A".repeat(31), "Content"),
                Arguments.of("Title", "A".repeat(2001))
        );
    }

    @Test
    @DisplayName("Student with null email should fail validation")
    void testStudentWithNullEmail() {
        assertThrows(InvalidDataException.class, () -> {
            new Student("Artem", "Fivko", null, LocalDate.now());
        });
    }

    @Test
    @DisplayName("Course with null startDate should fail validation")
    void testCourseWithNullStartDate() {
        assertThrows(InvalidDataException.class, () -> {
            new Course("Title", "Description", 3, null);
        });
    }

    @Test
    @DisplayName("Assignment with null dueDate should fail validation")
    void testAssignmentWithNullDueDate() {
        myModule module = new myModule("Test", "Content");
        assertThrows(InvalidDataException.class, () -> {
            new Assignment(module, null, 50, Mark.GOOD);
        });
    }
}
