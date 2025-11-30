package ua.onlinecourses.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.onlinecourses.model.Instructor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InstructorRepository Sorting Tests")
class InstructorRepositorySortingTest {

    private InstructorRepository instructorRepository;
    private Instructor instructor1;
    private Instructor instructor2;
    private Instructor instructor3;
    private Instructor instructor4;
    private Instructor instructor5;

    @BeforeEach
    void setUp() {
        instructorRepository = new InstructorRepository();

        instructor1 = new Instructor("Галина", "Унгурян", 26);
        instructor2 = new Instructor("Ігор", "Булат", 34);
        instructor3 = new Instructor("Іван", "Данилюк", 23);
        instructor4 = new Instructor("Анна", "Шепетюк", 40);
        instructor5 = new Instructor("Дмитро", "Плаксій", 29);

        instructorRepository.add(instructor1);
        instructorRepository.add(instructor2);
        instructorRepository.add(instructor3);
        instructorRepository.add(instructor4);
        instructorRepository.add(instructor5);
    }

    @Test
    @DisplayName("sortByExpertise should sort by expertise descending")
    void testSortByExpertise() {
        List<Instructor> sorted = instructorRepository.sortByExpertise();

        assertEquals(5, sorted.size());
        assertEquals(40, sorted.get(0).expertise());
        assertEquals(34, sorted.get(1).expertise());
        assertEquals(29, sorted.get(2).expertise());
        assertEquals(26, sorted.get(3).expertise());
        assertEquals(23, sorted.get(4).expertise());
    }

    @Test
    @DisplayName("sortByLastName should sort by lastName then firstName")
    void testSortByLastName() {
        List<Instructor> sorted = instructorRepository.sortByLastName();

        assertEquals(5, sorted.size());
        assertEquals("Булат", sorted.get(0).lastName());
        assertEquals("Данилюк", sorted.get(1).lastName());
        assertEquals("Плаксій", sorted.get(2).lastName());
        assertEquals("Унгурян", sorted.get(3).lastName());
        assertEquals("Шепетюк", sorted.get(4).lastName());
    }

    @Test
    @DisplayName("sortByFirstName should sort by firstName then lastName")
    void testSortByFirstName() {
        List<Instructor> sorted = instructorRepository.sortByFirstName();

        assertEquals(5, sorted.size());
        assertEquals("Іван", sorted.get(0).firstName());
        assertEquals("Ігор", sorted.get(1).firstName());
        assertEquals("Анна", sorted.get(2).firstName());
        assertEquals("Галина", sorted.get(3).firstName());
        assertEquals("Дмитро", sorted.get(4).firstName());
    }

    @Test
    @DisplayName("sortByLastNameLength should sort by lastName length")
    void testSortByLastNameLength() {
        List<Instructor> sorted = instructorRepository.sortByLastNameLength();

        assertEquals(5, sorted.size());

        for (int i = 0; i < sorted.size() - 1; i++) {
            int currentLength = sorted.get(i).lastName().length();
            int nextLength = sorted.get(i + 1).lastName().length();
            assertTrue(currentLength <= nextLength);
        }
    }

    @Test
    @DisplayName("sortByExpertiseAndName should sort by expertise desc then name")
    void testSortByExpertiseAndName() {
        List<Instructor> sorted = instructorRepository.sortByExpertiseAndName();

        assertEquals(5, sorted.size());
        assertEquals(40, sorted.get(0).expertise());
        assertEquals(34, sorted.get(1).expertise());
        assertEquals(29, sorted.get(2).expertise());
        assertEquals(26, sorted.get(3).expertise());
        assertEquals(23, sorted.get(4).expertise());

        assertEquals("Анна", sorted.get(0).firstName());
        assertEquals("Ігор", sorted.get(1).firstName());
    }

    @Test
    @DisplayName("sorting should not modify the original repository")
    void testSortingDoesNotModifyRepository() {
        List<Instructor> originalOrder = instructorRepository.getAll();

        instructorRepository.sortByExpertise();
        instructorRepository.sortByLastName();
        instructorRepository.sortByFirstName();

        List<Instructor> currentOrder = instructorRepository.getAll();

        assertEquals(originalOrder, currentOrder);
    }

    @Test
    @DisplayName("sorting empty repository should return empty list")
    void testSortingEmptyRepository() {
        InstructorRepository emptyRepo = new InstructorRepository();

        assertTrue(emptyRepo.sortByExpertise().isEmpty());
        assertTrue(emptyRepo.sortByLastName().isEmpty());
        assertTrue(emptyRepo.sortByFirstName().isEmpty());
    }

    @Test
    @DisplayName("sorting single instructor should return list with that instructor")
    void testSortingSingleInstructor() {
        InstructorRepository singleRepo = new InstructorRepository();
        Instructor instructor = new Instructor("Test", "Instructor", 45);
        singleRepo.add(instructor);

        List<Instructor> sortedByExp = singleRepo.sortByExpertise();
        assertEquals(1, sortedByExp.size());
        assertEquals(instructor, sortedByExp.get(0));

        List<Instructor> sortedByName = singleRepo.sortByLastName();
        assertEquals(1, sortedByName.size());
        assertEquals(instructor, sortedByName.get(0));
    }

    @Test
    @DisplayName("sortByExpertise with same expertise should maintain stable order")
    void testSortByExpertiseWithDuplicates() {
        InstructorRepository repo = new InstructorRepository();

        Instructor i1 = new Instructor("Тетяна", "Сопронюк", 45);
        Instructor i2 = new Instructor("Олександр", "Бігун", 45);
        Instructor i3 = new Instructor("Валерій", "Дудчак", 48);

        repo.add(i1);
        repo.add(i2);
        repo.add(i3);

        List<Instructor> sorted = repo.sortByExpertise();

        assertEquals(3, sorted.size());
        assertEquals(48, sorted.get(0).expertise());
        assertEquals(45, sorted.get(1).expertise());
        assertEquals(45, sorted.get(2).expertise());
    }

    @Test
    @DisplayName("findByLastName should work after sorting")
    void testFindAfterSort() {
        instructorRepository.sortByExpertise();

        List<Instructor> found = instructorRepository.findByLastName("Булат");

        assertEquals(1, found.size());
        assertEquals("Ігор", found.get(0).firstName());
    }

    @Test
    @DisplayName("sortByExpertiseAndName should handle multiple instructors with same expertise")
    void testSortByExpertiseAndNameWithSameExpertise() {
        InstructorRepository repo = new InstructorRepository();

        Instructor i1 = new Instructor("Олена", "Карлова", 45);
        Instructor i2 = new Instructor("Володимир", "Скрипа", 45);
        Instructor i3 = new Instructor("Денис", "Онипа", 45);

        repo.add(i1);
        repo.add(i2);
        repo.add(i3);

        List<Instructor> sorted = repo.sortByExpertiseAndName();

        assertEquals(3, sorted.size());
        assertEquals("Карлова", sorted.get(0).lastName());
        assertEquals("Онипа", sorted.get(1).lastName());
        assertEquals("Скрипа", sorted.get(2).lastName());
    }
}