package ua.onlinecourses.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.onlinecourses.model.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StudentRepository Sorting Tests")
class StudentRepositorySortingTest {
    private static final Logger logger = Logger.getLogger(StudentRepositorySortingTest.class.getName());

    private StudentRepository studentRepository;
    private Student studentAlice;
    private Student studentMaria;
    private Student studentAlex;
    private Student studentAliceH;
    private Student studentAnna;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data");
        studentRepository = new StudentRepository();

        studentAlice = new Student("Alice", "Rotar", "alice.rotar@chnu.edu.ua", LocalDate.of(2025, 9, 1));
        studentMaria = new Student("Maria", "Smyk", "maria.smyk@chnu.edu.ua", LocalDate.of(2025, 9, 5));
        studentAlex = new Student("Alex", "Kushnir", "alex.kushnir@chnu.edu.ua", LocalDate.of(2025, 9, 10));

        studentAliceH = new Student("Alice", "Hatsenko", "alice.hatsenko@chnu.edu.ua", LocalDate.of(2025, 9, 15));
        studentAnna = new Student("Anna", "Hatsenko", "anna.hatsenko@chnu.edu.ua", LocalDate.of(2025, 9, 20));

        studentRepository.add(studentMaria);
        studentRepository.add(studentAlice);
        studentRepository.add(studentAlex);
        studentRepository.add(studentAliceH);
        studentRepository.add(studentAnna);

        logger.info("Test setup completed with " + studentRepository.size() + " students");
    }

    @Test
    @DisplayName("sortByName should sort by last name, then first name, then email")
    void testSortByName() {
        logger.info("Testing sortByName");

        List<Student> sorted = studentRepository.sortByName();

        assertEquals(5, sorted.size(), "Should return all students");

        assertEquals("Hatsenko", sorted.get(0).lastName(), "First student should be Hatsenko");
        assertEquals("Hatsenko", sorted.get(1).lastName(), "Second student should be Hatsenko");
        assertEquals("Kushnir", sorted.get(2).lastName(), "Third student should be Kushnir");
        assertEquals("Rotar", sorted.get(3).lastName(), "Fourth student should be Rotar");
        assertEquals("Alice", sorted.get(3).firstName(), "Fourth student first name should be Alice");

        assertEquals("Smyk", sorted.get(4).lastName(), "Fifth student should be Smyk");
        assertEquals("Maria", sorted.get(4).firstName(), "Fifth student first name should be Maria");

        logger.info("sortByName test completed successfully");
    }

    @Test
    @DisplayName("sortByNameDesc should sort by last name descending, then first name, then email")
    void testSortByNameDesc() {
        logger.info("Testing sortByNameDesc");

        List<Student> sorted = studentRepository.sortByNameDesc();

        assertEquals(5, sorted.size(), "Should return all students");

        assertEquals("Smyk", sorted.get(0).lastName(), "First student should be Smyk");
        assertEquals("Maria", sorted.get(0).firstName(), "First Hatsenko should be Maria");

        assertEquals("Rotar", sorted.get(1).lastName(), "Second student should be Rotar");
        assertEquals("Alice", sorted.get(1).firstName(), "Second Hatsenko should be Alice");

        assertEquals("Kushnir", sorted.get(2).lastName(), "Third student should be Kushnir");
        assertEquals("Hatsenko", sorted.get(3).lastName(), "Fourth student should be Hatsenko");
        assertEquals("Hatsenko", sorted.get(4).lastName(), "Fifth student should be Hatsenko");

        logger.info("sortByNameDesc test completed successfully");
    }

    @Test
    @DisplayName("sortByEnrollmentDate should sort by enrollment date ascending")
    void testSortByEnrollmentDate() {
        logger.info("Testing sortByEnrollmentDate");

        List<Student> sorted = studentRepository.sortByEnrollmentDate();

        assertEquals(5, sorted.size(), "Should return all students");

        assertEquals(LocalDate.of(2025, 9, 1), sorted.get(0).enrollmentDate());
        assertEquals(LocalDate.of(2025, 9, 5), sorted.get(1).enrollmentDate());
        assertEquals(LocalDate.of(2025, 9, 10), sorted.get(2).enrollmentDate());
        assertEquals(LocalDate.of(2025, 9, 15), sorted.get(3).enrollmentDate());
        assertEquals(LocalDate.of(2025, 9, 20), sorted.get(4).enrollmentDate());

        logger.info("sortByEnrollmentDate test completed successfully");
    }

    @Test
    @DisplayName("sortByName should not modify the original repository")
    void testSortByNameDoesNotModifyRepository() {
        logger.info("Testing that sortByName does not modify repository");

        List<Student> originalOrder = studentRepository.getAll();
        List<Student> sorted = studentRepository.sortByName();
        List<Student> currentOrder = studentRepository.getAll();

        assertEquals(originalOrder, currentOrder, "Repository order should remain unchanged");
        assertNotSame(sorted, currentOrder, "Sorted list should be a different instance");
    }

    @Test
    @DisplayName("sortByName with students having same last and first name should sort by email")
    void testSortByNameWithIdenticalNames() {
        logger.info("Testing sortByName with identical names");

        StudentRepository repo = new StudentRepository();
        Student student1 = new Student("Vasyl", "Kosovan", "vasyl.kosovan.c@chnu.edu.ua", LocalDate.of(2023, 1, 1));
        Student student2 = new Student("Vasyl", "Kosovan", "vasyl.kosovan.a@chnu.edu.ua", LocalDate.of(2023, 1, 1));
        Student student3 = new Student("Vasyl", "Kosovan", "vasyl.kosovan.b@chnu.edu.ua", LocalDate.of(2023, 1, 1));

        repo.add(student1);
        repo.add(student2);
        repo.add(student3);

        List<Student> sorted = repo.sortByName();

        assertEquals("vasyl.kosovan.a@chnu.edu.ua", sorted.get(0).email(), "First should be sorted by email: a");
        assertEquals("vasyl.kosovan.b@chnu.edu.ua", sorted.get(1).email(), "Second should be sorted by email: b");
        assertEquals("vasyl.kosovan.c@chnu.edu.ua", sorted.get(2).email(), "Third should be sorted by email: c");

        logger.info("sortByName with identical names test completed");
    }

    @Test
    @DisplayName("Sorting empty repository should return empty list")
    void testSortingEmptyRepository() {
        logger.info("Testing sorting on empty repository");

        StudentRepository emptyRepo = new StudentRepository();

        assertTrue(emptyRepo.sortByName().isEmpty(), "sortByName on empty repository should return empty list");
        assertTrue(emptyRepo.sortByNameDesc().isEmpty(), "sortByNameDesc on empty repository should return empty list");
        assertTrue(emptyRepo.sortByEnrollmentDate().isEmpty(), "sortByEnrollmentDate on empty repository should return empty list");

        logger.info("Empty repository sorting test completed");
    }

    @Test
    @DisplayName("Sorting single student should return list with that student")
    void testSortingSingleStudent() {
        logger.info("Testing sorting with single student");

        StudentRepository singleRepo = new StudentRepository();
        Student student = new Student("Alice", "Rotar", "alice.rotar@chnu.edu.ua", LocalDate.of(2025, 1, 1));
        singleRepo.add(student);

        List<Student> sortedByName = singleRepo.sortByName();
        assertEquals(1, sortedByName.size(), "sortByName with single student should have size 1");
        assertEquals(student, sortedByName.get(0), "Should contain the single student");

        List<Student> sortedByDate = singleRepo.sortByEnrollmentDate();
        assertEquals(1, sortedByDate.size(), "sortByEnrollmentDate with single student should have size 1");
        assertEquals(student, sortedByDate.get(0), "Should contain the single student");

        logger.info("Single student sorting test completed");
    }

    @Test
    @DisplayName("sortByEmailLength should sort by email length")
    void testSortByEmailLength() {
        logger.info("Testing sortByEmailLength");

        List<Student> sorted = studentRepository.sortByEmailLength();

        assertEquals(5, sorted.size(), "Should return all students");

        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue(sorted.get(i).email().length() <= sorted.get(i + 1).email().length(),
                    "Email should be sorted by length");
        }

        logger.info("sortByEmailLength test completed successfully");
    }

    @Test
    @DisplayName("sortByFirstName should sort by first name")
    void testSortByFirstName() {
        logger.info("Testing sortByFirstName");

        List<Student> sorted = studentRepository.sortByFirstName();

        assertEquals(5, sorted.size(), "Should return all students");

        assertEquals("Alex", sorted.get(0).firstName(), "First should be Alex");
        assertEquals("Alice", sorted.get(1).firstName(), "Second should be Alice");
        assertEquals("Anna", sorted.get(3).firstName(), "Fourth should be Bob");
        assertEquals("Maria", sorted.get(4).firstName(), "Fifth should be Maria");

        logger.info("sortByFirstName test completed successfully");
    }
}