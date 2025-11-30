package ua.onlinecourses.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarkTest {

    @Test
    void testGetValue() {
        assertEquals(5, Mark.EXCELLENT.getValue());
        assertEquals(4, Mark.GOOD.getValue());
        assertEquals(3, Mark.PASSED.getValue());
        assertEquals(2, Mark.SATISFACTORY.getValue());
        assertEquals(1, Mark.LOW.getValue());
        assertEquals(0, Mark.NOT_PASSED.getValue());
    }
}