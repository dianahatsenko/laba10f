package ua.onlinecourses.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

import ua.onlinecourses.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@DisplayName("Generic Repository Tests")
public class GenericRepositoryTest {

    private GenericRepository<Student> studentRepository;
    private GenericRepository<Course> courseRepository;
    private GenericRepository<Instructor> instructorRepository;
    private GenericRepository<myModule> moduleRepository;

    private Student student1, student2, student3;
    private Course course1, course2;
    private Instructor instructor1, instructor2;
    private myModule module1, module2;

    @BeforeEach
    void setUp() {
        studentRepository = new GenericRepository<>(Student::email, "Student");
        courseRepository = new GenericRepository<>(Course::getFullName, "Course");
        instructorRepository = new GenericRepository<>(Instructor::getFullName, "Instructor");
        moduleRepository = new GenericRepository<>(myModule::getFullName, "Module");

        student1 = new Student("Yuriy", "Vasuluk", "yuriy.vasuluk@chnu.edu.ua", LocalDate.of(2023, 9, 1));
        student2 = new Student("Alice", "Rotar", "alice.rotar@chnu.edu.ua", LocalDate.of(2024, 1, 15));
        student3 = new Student("Mariya", "Shevchuk", "mariya.shevchuk@chnu.edu.ua", LocalDate.of(2024, 2, 20));

        course1 = new Course("Java Programming", "Learn Java basics", 5, LocalDate.of(2025, 3, 1));
        course2 = new Course("Data Structures", "Advanced algorithms", 4, LocalDate.of(2025, 4, 15));

        instructor1 = new Instructor("Haluna", "Melnuk", 15);
        instructor2 = new Instructor("Vasyl", "Kosovan", 20);

        module1 = new myModule("Introduction", "Basic concepts");
        module2 = new myModule("Advanced Topics", "Deep dive");
    }

    @Test
    @DisplayName("Test adding valid student")
    void testAddValidStudent() {
        boolean added = studentRepository.add(student1);

        assertTrue(added);
        assertEquals(1, studentRepository.size());
        assertTrue(studentRepository.contains(student1));
    }

    @Test
    @DisplayName("Test adding multiple students")
    void testAddMultipleStudents() {
        studentRepository.add(student1);
        studentRepository.add(student2);
        studentRepository.add(student3);

        assertEquals(3, studentRepository.size());
        assertTrue(studentRepository.contains(student1));
        assertTrue(studentRepository.contains(student2));
        assertTrue(studentRepository.contains(student3));
    }

    @Test
    @DisplayName("Test adding duplicate student")
    void testAddDuplicateStudent() {
        studentRepository.add(student1);

        Student duplicateStudent = new Student("Anna", "Lozinska", "yuriy.vasuluk@chnu.edu.ua", LocalDate.of(2024, 3, 1));
        boolean added = studentRepository.add(duplicateStudent);

        assertFalse(added);
        assertEquals(1, studentRepository.size());
    }

    @Test
    @DisplayName("Test adding null student")
    void testAddNullStudent() {
        boolean added = studentRepository.add(null);

        assertFalse(added);
        assertEquals(0, studentRepository.size());
    }

    @Test
    @DisplayName("Test adding valid course")
    void testAddValidCourse() {
        boolean added = courseRepository.add(course1);

        assertTrue(added);
        assertEquals(1, courseRepository.size());
    }

    @Test
    @DisplayName("Test adding duplicate course")
    void testAddDuplicateCourse() {
        courseRepository.add(course1);

        Course duplicateCourse = new Course("Java Programming", "Learn Java basics", 5, LocalDate.of(2025, 3, 1));
        boolean added = courseRepository.add(duplicateCourse);

        assertFalse(added);
        assertEquals(1, courseRepository.size());
    }

    @Test
    @DisplayName("Test finding existing student by identity")
    void testFindExistingStudent() {
        studentRepository.add(student2);

        Optional<Student> found = studentRepository.findByIdentity("alice.rotar@chnu.edu.ua");

        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().firstName());
        assertEquals("Rotar", found.get().lastName());
    }

    @Test
    @DisplayName("Test finding non-existing student")
    void testFindNonExistingStudent() {
        studentRepository.add(student1);

        Optional<Student> found = studentRepository.findByIdentity("anastasiya.vin@chnu.edu.ua");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Test finding with null identity")
    void testFindWithNullIdentity() {
        studentRepository.add(student1);

        Optional<Student> found = studentRepository.findByIdentity(null);

        assertFalse(found.isPresent());
    }

    @ParameterizedTest
    @ValueSource(strings = {"yuriy.vasuluk@chnu.edu.ua", "alice.rotar@chnu.edu.ua", "mariya.shevchuk@chnu.edu.ua"})
    @DisplayName("Test finding multiple students by email")
    void testFindMultipleStudentsByEmail(String email) {
        studentRepository.add(student1);
        studentRepository.add(student2);
        studentRepository.add(student3);

        Optional<Student> found = studentRepository.findByIdentity(email);

        assertTrue(found.isPresent());
        assertEquals(email, found.get().email());
    }

    @Test
    @DisplayName("Test containsIdentity for existing item")
    void testContainsIdentityExisting() {
        instructorRepository.add(instructor1);

        assertTrue(instructorRepository.containsIdentity(instructor1.getFullName()));
    }

    @Test
    @DisplayName("Test containsIdentity for non-existing item")
    void testContainsIdentityNonExisting() {
        instructorRepository.add(instructor1);

        assertFalse(instructorRepository.containsIdentity("NONEXISTENT"));
    }

    @Test
    @DisplayName("Test removing student by object")
    void testRemoveStudentByObject() {
        studentRepository.add(student1);
        studentRepository.add(student2);

        boolean removed = studentRepository.remove(student1);

        assertTrue(removed);
        assertEquals(1, studentRepository.size());
        assertFalse(studentRepository.contains(student1));
        assertTrue(studentRepository.contains(student2));
    }

    @Test
    @DisplayName("Test removing student by identity")
    void testRemoveStudentByIdentity() {
        studentRepository.add(student1);
        studentRepository.add(student2);

        boolean removed = studentRepository.removeByIdentity("yuriy.vasuluk@chnu.edu.ua");

        assertTrue(removed);
        assertEquals(1, studentRepository.size());
        assertFalse(studentRepository.containsIdentity("yuriy.vasuluk@chnu.edu.ua"));
    }

    @Test
    @DisplayName("Test removing non-existing student")
    void testRemoveNonExistingStudent() {
        studentRepository.add(student1);

        boolean removed = studentRepository.removeByIdentity("nonexistent@test.com");

        assertFalse(removed);
        assertEquals(1, studentRepository.size());
    }

    @Test
    @DisplayName("Test removing with null identity")
    void testRemoveWithNullIdentity() {
        studentRepository.add(student1);

        boolean removed = studentRepository.removeByIdentity(null);

        assertFalse(removed);
        assertEquals(1, studentRepository.size());
    }

    @Test
    @DisplayName("Test removing null object")
    void testRemoveNullObject() {
        studentRepository.add(student1);

        boolean removed = studentRepository.remove(null);

        assertFalse(removed);
        assertEquals(1, studentRepository.size());
    }

    @Test
    @DisplayName("Test removing from empty repository")
    void testRemoveFromEmptyRepository() {
        boolean removed = studentRepository.removeByIdentity("any@test.com");

        assertFalse(removed);
        assertEquals(0, studentRepository.size());
    }


    @Test
    @DisplayName("Test getAll from empty repository")
    void testGetAllEmpty() {
        List<Student> allStudents = studentRepository.getAll();

        assertNotNull(allStudents);
        assertTrue(allStudents.isEmpty());
        assertEquals(0, allStudents.size());
    }

    @Test
    @DisplayName("Test getAll with multiple items")
    void testGetAllMultipleItems() {
        moduleRepository.add(module1);
        moduleRepository.add(module2);

        List<myModule> allModules = moduleRepository.getAll();

        assertEquals(2, allModules.size());
        assertTrue(allModules.contains(module1));
        assertTrue(allModules.contains(module2));
    }

    @Test
    @DisplayName("Test getAll returns defensive copy")
    void testGetAllReturnsDefensiveCopy() {
        studentRepository.add(student1);
        studentRepository.add(student2);

        List<Student> allStudents = studentRepository.getAll();
        allStudents.clear();

        assertEquals(2, studentRepository.size());
        assertEquals(2, studentRepository.getAll().size());
    }

    @Test
    @DisplayName("Test clear repository")
    void testClearRepository() {
        courseRepository.add(course1);
        courseRepository.add(course2);

        assertEquals(2, courseRepository.size());

        courseRepository.clear();

        assertEquals(0, courseRepository.size());
        assertTrue(courseRepository.isEmpty());
        assertTrue(courseRepository.getAll().isEmpty());
    }

    @Test
    @DisplayName("Test clear empty repository")
    void testClearEmptyRepository() {
        courseRepository.clear();

        assertEquals(0, courseRepository.size());
        assertTrue(courseRepository.isEmpty());
    }

    @Test
    @DisplayName("Test repository after clear")
    void testRepositoryAfterClear() {
        studentRepository.add(student1);
        studentRepository.clear();

        boolean added = studentRepository.add(student2);

        assertTrue(added);
        assertEquals(1, studentRepository.size());
    }


    @Test
    @DisplayName("Test size of empty repository")
    void testSizeEmpty() {
        assertEquals(0, instructorRepository.size());
    }

    @Test
    @DisplayName("Test size after adding items")
    void testSizeAfterAdding() {
        instructorRepository.add(instructor1);
        assertEquals(1, instructorRepository.size());

        instructorRepository.add(instructor2);
        assertEquals(2, instructorRepository.size());
    }

    @Test
    @DisplayName("Test isEmpty on empty repository")
    void testIsEmptyTrue() {
        assertTrue(studentRepository.isEmpty());
    }

    @Test
    @DisplayName("Test isEmpty on non-empty repository")
    void testIsEmptyFalse() {
        studentRepository.add(student1);

        assertFalse(studentRepository.isEmpty());
    }


    @Test
    @DisplayName("Test complete workflow: add, find, remove")
    void testCompleteWorkflow() {
        studentRepository.add(student1);
        studentRepository.add(student2);
        assertEquals(2, studentRepository.size());

        Optional<Student> found = studentRepository.findByIdentity("alice.rotar@chnu.edu.ua");
        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().firstName());

        studentRepository.removeByIdentity("alice.rotar@chnu.edu.ua");
        assertEquals(1, studentRepository.size());

        Optional<Student> notFound = studentRepository.findByIdentity("alice.rotar@chnu.edu.ua");
        assertFalse(notFound.isPresent());

        studentRepository.clear();
        assertTrue(studentRepository.isEmpty());
    }

    @Test
    @DisplayName("Test multiple repositories independence")
    void testMultipleRepositoriesIndependence() {
        studentRepository.add(student1);
        courseRepository.add(course1);
        instructorRepository.add(instructor1);

        assertEquals(1, studentRepository.size());
        assertEquals(1, courseRepository.size());
        assertEquals(1, instructorRepository.size());

        studentRepository.clear();

        assertEquals(0, studentRepository.size());
        assertEquals(1, courseRepository.size());
        assertEquals(1, instructorRepository.size());
    }

    @ParameterizedTest
    @MethodSource("provideRepositoryTestData")
    @DisplayName("Test different entity types in repository")
    void testDifferentEntityTypes(String entityType, Object item, String identity) {
        GenericRepository<Object> repo = new GenericRepository<>(obj -> identity, entityType);

        boolean added = repo.add(item);
        assertTrue(added);

        Optional<Object> found = repo.findByIdentity(identity);
        assertTrue(found.isPresent());
    }

    private static Stream<Arguments> provideRepositoryTestData() {
        return Stream.of(
                Arguments.of("Student",
                        new Student("Dina", "Krasova", "dina.krasova@chnu.edu.ua", LocalDate.now()),
                        "dina.krasova@chnu.edu.ua"),
                Arguments.of("Course",
                        new Course("Physical education", "Sports activity", 3, LocalDate.now().plusMonths(1)),
                        "PHY-SPO-3" + LocalDate.now().plusMonths(1)),
                Arguments.of("Instructor",
                        new Instructor("Natalia", "Marinova", 10),
                        "NATMARI-10")
        );
    }

    @Test
    @DisplayName("Test contains method")
    void testContainsMethod() {
        studentRepository.add(student1);

        assertTrue(studentRepository.contains(student1));
        assertFalse(studentRepository.contains(student2));
    }

    @Test
    @DisplayName("Test repository with custom identity extractor")
    void testCustomIdentityExtractor() {
        GenericRepository<Student> customRepo = new GenericRepository<>(
                student -> student.firstName() + "-" + student.lastName(),
                "CustomStudent"
        );

        customRepo.add(student1);

        Optional<Student> found = customRepo.findByIdentity("Yuriy-Vasuluk");
        assertTrue(found.isPresent());
        assertEquals("Yuriy", found.get().firstName());
    }
}