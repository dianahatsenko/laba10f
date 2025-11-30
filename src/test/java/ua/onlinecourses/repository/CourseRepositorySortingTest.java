package ua.onlinecourses.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.onlinecourses.model.Course;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CourseRepository Sorting Tests")
class CourseRepositorySortingTest {
    private static final Logger logger = Logger.getLogger(CourseRepositorySortingTest.class.getName());

    private CourseRepository courseRepository;
    private Course course1;
    private Course course2;
    private Course course3;
    private Course course4;
    private Course course5;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data");
        courseRepository = new CourseRepository();

        course1 = new Course("Java Programming", "Introduction to Java", 5, LocalDate.of(2025, 1, 15));
        course2 = new Course("Data Structures", "Advanced algorithms", 3, LocalDate.of(2025, 2, 1));
        course3 = new Course("Web Development", "HTML, CSS, JavaScript", 4, LocalDate.of(2025, 1, 20));
        course4 = new Course("Database Systems", "SQL and NoSQL", 5, LocalDate.of(2025, 3, 1));
        course5 = new Course("Machine Learning", "AI fundamentals", 2, LocalDate.of(2025, 2, 15));

        courseRepository.add(course1);
        courseRepository.add(course2);
        courseRepository.add(course3);
        courseRepository.add(course4);
        courseRepository.add(course5);

        logger.info("Test setup completed with " + courseRepository.size() + " courses");
    }

    @Test
    @DisplayName("sortByTitle should sort courses by title")
    void testSortByTitle() {
        logger.info("Testing sortByTitle");

        List<Course> sorted = courseRepository.sortByTitle();

        assertEquals(5, sorted.size(), "Should return all courses");

        assertEquals("Data Structures", sorted.get(0).title(), "First should be Data Structures");
        assertEquals("Database Systems", sorted.get(1).title(), "Second should be Database Systems");
        assertEquals("Java Programming", sorted.get(2).title(), "Third should be Java Programming");
        assertEquals("Machine Learning", sorted.get(3).title(), "Fourth should be Machine Learning");
        assertEquals("Web Development", sorted.get(4).title(), "Fifth should be Web Development");

        logger.info("sortByTitle test completed successfully");
    }

    @Test
    @DisplayName("sortByCredits should sort courses by credits ascending")
    void testSortByCredits() {
        logger.info("Testing sortByCredits");

        List<Course> sorted = courseRepository.sortByCredits();

        assertEquals(5, sorted.size(), "Should return all courses");

        assertEquals(2, sorted.get(0).credits(), "First should have 2 credits");
        assertEquals(3, sorted.get(1).credits(), "Second should have 3 credits");
        assertEquals(4, sorted.get(2).credits(), "Third should have 4 credits");
        assertEquals(5, sorted.get(3).credits(), "Fourth should have 5 credits");
        assertEquals(5, sorted.get(4).credits(), "Fifth should have 5 credits");


        logger.info("sortByCredits test completed successfully");
    }

    @Test
    @DisplayName("sortByCreditsDesc should sort courses by credits descending")
    void testSortByCreditsDesc() {
        logger.info("Testing sortByCreditsDesc");

        List<Course> sorted = courseRepository.sortByCreditsDesc();

        assertEquals(5, sorted.size(), "Should return all courses");


        assertEquals(5, sorted.get(0).credits(), "First should have 5 credits");
        assertEquals(5, sorted.get(1).credits(), "Second should have 5 credits");
        assertEquals(4, sorted.get(2).credits(), "Third should have 4 credits");
        assertEquals(3, sorted.get(3).credits(), "Fourth should have 3 credits");
        assertEquals(2, sorted.get(4).credits(), "Fifth should have 2 credits");

        logger.info("sortByCreditsDesc test completed successfully");
    }

    @Test
    @DisplayName("sortByStartDate should sort courses by start date")
    void testSortByStartDate() {
        logger.info("Testing sortByStartDate");

        List<Course> sorted = courseRepository.sortByStartDate();

        assertEquals(5, sorted.size(), "Should return all courses");

        assertEquals(LocalDate.of(2025, 1, 15), sorted.get(0).startDate(), "First should start on Jan 15");
        assertEquals(LocalDate.of(2025, 1, 20), sorted.get(1).startDate(), "Second should start on Jan 20");
        assertEquals(LocalDate.of(2025, 2, 1), sorted.get(2).startDate(), "Third should start on Feb 1");
        assertEquals(LocalDate.of(2025, 2, 15), sorted.get(3).startDate(), "Fourth should start on Feb 15");
        assertEquals(LocalDate.of(2025, 3, 1), sorted.get(4).startDate(), "Fifth should start on Mar 1");

        logger.info("sortByStartDate test completed successfully");
    }

    @Test
    @DisplayName("sortByDescription should sort courses by description using lambda")
    void testSortByDescription() {
        logger.info("Testing sortByDescription");

        List<Course> sorted = courseRepository.sortByDescription();

        assertEquals(5, sorted.size(), "Should return all courses");

        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue(sorted.get(i).description().compareTo(sorted.get(i + 1).description()) <= 0,
                    "Descriptions should be sorted alphabetically");
        }

        logger.info("sortByDescription test completed successfully");
    }

    @Test
    @DisplayName("sortByCreditsAndDate should sort by credits desc, then by date")
    void testSortByCreditsAndDate() {
        logger.info("Testing sortByCreditsAndDate");

        List<Course> sorted = courseRepository.sortByCreditsAndDate();

        assertEquals(5, sorted.size(), "Should return all courses");

        assertEquals(5, sorted.get(0).credits(), "First should have 5 credits");
        assertEquals(5, sorted.get(1).credits(), "Second should have 5 credits");

        assertTrue(sorted.get(0).startDate().isBefore(sorted.get(1).startDate()),
                "5-credit courses should be sorted by date");

        assertEquals(4, sorted.get(2).credits(), "Third should have 4 credits");
        assertEquals(3, sorted.get(3).credits(), "Fourth should have 3 credits");
        assertEquals(2, sorted.get(4).credits(), "Fifth should have 2 credits");

        logger.info("sortByCreditsAndDate test completed successfully");
    }

    @Test
    @DisplayName("Sorting should not modify the original repository")
    void testSortingDoesNotModifyRepository() {
        logger.info("Testing that sorting does not modify repository");

        List<Course> originalOrder = courseRepository.getAll();
        courseRepository.sortByTitle();
        courseRepository.sortByCredits();
        List<Course> currentOrder = courseRepository.getAll();

        assertEquals(originalOrder, currentOrder, "Repository order should remain unchanged");

        logger.info("Repository immutability test completed");
    }

    @Test
    @DisplayName("Sorting empty repository should return empty list")
    void testSortingEmptyRepository() {
        logger.info("Testing sorting on empty repository");

        CourseRepository emptyRepo = new CourseRepository();

        assertTrue(emptyRepo.sortByTitle().isEmpty(), "sortByTitle on empty repository should return empty list");
        assertTrue(emptyRepo.sortByCredits().isEmpty(), "sortByCredits on empty repository should return empty list");
        assertTrue(emptyRepo.sortByStartDate().isEmpty(), "sortByStartDate on empty repository should return empty list");

        logger.info("Empty repository sorting test completed");
    }

    @Test
    @DisplayName("sortByIdentity should sort by fullName")
    void testSortByIdentity() {
        logger.info("Testing sortByIdentity");

        List<Course> sortedAsc = courseRepository.sortByIdentity("asc");
        List<Course> sortedDesc = courseRepository.sortByIdentity("desc");

        assertEquals(5, sortedAsc.size(), "Should return all courses");
        assertEquals(5, sortedDesc.size(), "Should return all courses");

        for (int i = 0; i < sortedAsc.size(); i++) {
            assertEquals(sortedAsc.get(i), sortedDesc.get(sortedDesc.size() - 1 - i),
                    "Ascending and descending should be reversed");
        }

        logger.info("sortByIdentity test completed successfully");
    }
}