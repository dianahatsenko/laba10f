package ua.onlinecourses.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

import ua.onlinecourses.exception.InvalidDataException;
import ua.onlinecourses.model.Assignment;
import ua.onlinecourses.model.myModule;
import ua.onlinecourses.model.Mark;

import java.time.LocalDate;
import java.util.stream.Stream;

public class AssignmentTest {

    @ParameterizedTest
    @MethodSource("provideValidAssignmentData")
    void testValidAssignmentCreation(String moduleTitle, String moduleContent, LocalDate dueDate, int maxPoints, Mark mark) {
        myModule module = new myModule(moduleTitle, moduleContent);

        assertDoesNotThrow(() -> {
            Assignment assignment = new Assignment(module, dueDate, maxPoints, mark);
            assertEquals(module, assignment.module());
            assertEquals(dueDate, assignment.dueDate());
            assertEquals(maxPoints, assignment.maxPoints());
            assertEquals(mark, assignment.mark());
        });
    }

    private static Stream<Arguments> provideValidAssignmentData() {
        LocalDate today = LocalDate.now();
        return Stream.of(
                Arguments.of("Introduction", "Basic concepts", today.plusDays(7), 100, Mark.EXCELLENT),
                Arguments.of("Advanced", "Complex topics", today.plusMonths(1), 50, Mark.GOOD),
                Arguments.of("Final Exam", "Comprehensive test", today.minusMonths(1), 80, Mark.PASSED)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10, 101, 200})
    void testInvalidMaxPoints(int invalidMaxPoints) {
        myModule module = new myModule("Test Module", "Test content");
        LocalDate dueDate = LocalDate.now().plusDays(7);

        assertThrows(InvalidDataException.class, () -> {
            new Assignment(module, dueDate, invalidMaxPoints, Mark.EXCELLENT);
        });
    }

    @Test
    void testInvalidDueDateTooOld() {
        myModule module = new myModule("Test Module", "Test content");
        LocalDate tooOldDate = LocalDate.now().minusYears(2);

        assertThrows(InvalidDataException.class, () -> {
            new Assignment(module, tooOldDate, 100, Mark.EXCELLENT);
        });
    }

    @Test
    void testInvalidDueDateTooFar() {
        myModule module = new myModule("Test Module", "Test content");
        LocalDate tooFarDate = LocalDate.now().plusYears(2);

        assertThrows(InvalidDataException.class, () -> {
            new Assignment(module, tooFarDate, 100, Mark.EXCELLENT);
        });
    }

    @ParameterizedTest
    @MethodSource("provideMarkTestData")
    void testGetMark(Mark mark, String expectedMessage) {
        myModule module = new myModule("Test Module", "Test content");
        LocalDate dueDate = LocalDate.now().plusDays(7);
        Assignment assignment = new Assignment(module, dueDate, 100, mark);

        assertEquals(expectedMessage, assignment.getMark());
    }

    private static Stream<Arguments> provideMarkTestData() {
        return Stream.of(
                Arguments.of(Mark.EXCELLENT, "Your mark is excellent"),
                Arguments.of(Mark.GOOD, "Your mark is good."),
                Arguments.of(Mark.PASSED, "You passed the exam"),
                Arguments.of(Mark.SATISFACTORY, "Your mark is satisfactory."),
                Arguments.of(Mark.LOW, "Your mark is low."),
                Arguments.of(Mark.NOT_PASSED, "You did not pass the exam.")
        );
    }

    @Test
    void testMarkEnumValues() {
        assertEquals(5, Mark.EXCELLENT.getValue());
        assertEquals(4, Mark.GOOD.getValue());
        assertEquals(3, Mark.PASSED.getValue());
        assertEquals(2, Mark.SATISFACTORY.getValue());
        assertEquals(1, Mark.LOW.getValue());
        assertEquals(0, Mark.NOT_PASSED.getValue());
    }

    @Test
    void testValidMaxPointsBoundary() {
        myModule module = new myModule("Test Module", "Test content");
        LocalDate dueDate = LocalDate.now().plusDays(7);

        assertDoesNotThrow(() -> {
            new Assignment(module, dueDate, 1, Mark.EXCELLENT);
            new Assignment(module, dueDate, 100, Mark.EXCELLENT);
        });
    }

    @Test
    void testValidDueDateBoundary() {
        myModule module = new myModule("Test Module", "Test content");
        LocalDate minDate = LocalDate.now().minusYears(1).plusDays(1);
        LocalDate maxDate = LocalDate.now().plusYears(1).minusDays(1);

        assertDoesNotThrow(() -> {
            new Assignment(module, minDate, 100, Mark.EXCELLENT);
            new Assignment(module, maxDate, 100, Mark.EXCELLENT);
        });
    }

    @Test
    void testAssignmentWithNullModule() {
        LocalDate dueDate = LocalDate.now().plusDays(7);

        assertDoesNotThrow(() -> {
            new Assignment(null, dueDate, 100, Mark.EXCELLENT);
        });
    }

    @Test
    void testAssignmentWithNullMark() {
        myModule module = new myModule("Test Module", "Test content");
        LocalDate dueDate = LocalDate.now().plusDays(7);

        assertDoesNotThrow(() -> {
            new Assignment(module, dueDate, 100, null);
        });
    }
}