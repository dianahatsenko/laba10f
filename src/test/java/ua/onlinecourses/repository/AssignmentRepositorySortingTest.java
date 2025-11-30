package ua.onlinecourses.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.onlinecourses.model.Assignment;
import ua.onlinecourses.model.Mark;
import ua.onlinecourses.model.myModule;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AssignmentRepository Sorting Tests")
class AssignmentRepositorySortingTest {

    private AssignmentRepository assignmentRepository;
    private myModule module1;
    private myModule module2;
    private myModule module3;
    private myModule module4;
    private Assignment assignment1;
    private Assignment assignment2;
    private Assignment assignment3;
    private Assignment assignment4;
    private Assignment assignment5;

    @BeforeEach
    void setUp() {
        assignmentRepository = new AssignmentRepository();

        module1 = new myModule("Вступ до Java", "Основні концепції ООП");
        module2 = new myModule("Колекції", "List, Set, Map");
        module3 = new myModule("Потоки вводу-виводу", "IO Streams");
        module4 = new myModule("Багатопоточність", "Threads");

        assignment1 = new Assignment(module1, LocalDate.of(2025, 5, 10), 100, Mark.EXCELLENT);
        assignment2 = new Assignment(module2, LocalDate.of(2025, 5, 15), 80, Mark.GOOD);
        assignment3 = new Assignment(module3, LocalDate.of(2025, 5, 5), 90, Mark.PASSED);
        assignment4 = new Assignment(module4, LocalDate.of(2025, 5, 20), 85, Mark.SATISFACTORY);
        assignment5 = new Assignment(module1, LocalDate.of(2025, 5, 8), 75, Mark.LOW);

        assignmentRepository.add(assignment1);
        assignmentRepository.add(assignment2);
        assignmentRepository.add(assignment3);
        assignmentRepository.add(assignment4);
        assignmentRepository.add(assignment5);
    }

    @Test
    @DisplayName("sortByDueDate should sort by due date ascending")
    void testSortByDueDate() {
        List<Assignment> sorted = assignmentRepository.sortByDueDate();

        assertEquals(5, sorted.size());
        assertEquals(LocalDate.of(2025, 5, 5), sorted.get(0).dueDate());
        assertEquals(LocalDate.of(2025, 5, 8), sorted.get(1).dueDate());
        assertEquals(LocalDate.of(2025, 5, 10), sorted.get(2).dueDate());
        assertEquals(LocalDate.of(2025, 5, 15), sorted.get(3).dueDate());
        assertEquals(LocalDate.of(2025, 5, 20), sorted.get(4).dueDate());
    }

    @Test
    @DisplayName("sortByDueDateDesc should sort by due date descending")
    void testSortByDueDateDesc() {
        List<Assignment> sorted = assignmentRepository.sortByDueDateDesc();

        assertEquals(5, sorted.size());
        assertEquals(LocalDate.of(2025, 5, 20), sorted.get(0).dueDate());
        assertEquals(LocalDate.of(2025, 5, 15), sorted.get(1).dueDate());
        assertEquals(LocalDate.of(2025, 5, 10), sorted.get(2).dueDate());
        assertEquals(LocalDate.of(2025, 5, 8), sorted.get(3).dueDate());
        assertEquals(LocalDate.of(2025, 5, 5), sorted.get(4).dueDate());
    }

    @Test
    @DisplayName("sortByMaxPoints should sort by max points descending")
    void testSortByMaxPoints() {
        List<Assignment> sorted = assignmentRepository.sortByMaxPoints();

        assertEquals(5, sorted.size());
        assertEquals(100, sorted.get(0).maxPoints());
        assertEquals(90, sorted.get(1).maxPoints());
        assertEquals(85, sorted.get(2).maxPoints());
        assertEquals(80, sorted.get(3).maxPoints());
        assertEquals(75, sorted.get(4).maxPoints());
    }

    @Test
    @DisplayName("sortByMark should sort by mark")
    void testSortByMark() {
        List<Assignment> sorted = assignmentRepository.sortByMark();

        assertEquals(5, sorted.size());

        for (int i = 0; i < sorted.size() - 1; i++) {
            Mark current = sorted.get(i).mark();
            Mark next = sorted.get(i + 1).mark();
            assertTrue(current.compareTo(next) <= 0);
        }
    }

    @Test
    @DisplayName("sortByModuleAndDate should sort by module then date")
    void testSortByModuleAndDate() {
        List<Assignment> sorted = assignmentRepository.sortByModuleAndDate();

        assertEquals(5, sorted.size());

        for (int i = 0; i < sorted.size() - 1; i++) {
            String currentModule = sorted.get(i).module().getFullName();
            String nextModule = sorted.get(i + 1).module().getFullName();

            if (currentModule.equals(nextModule)) {
                LocalDate currentDate = sorted.get(i).dueDate();
                LocalDate nextDate = sorted.get(i + 1).dueDate();
                assertTrue(currentDate.isBefore(nextDate) || currentDate.equals(nextDate));
            }
        }
    }

    @Test
    @DisplayName("sortByModuleTitle should sort by module title using lambda")
    void testSortByModuleTitle() {
        List<Assignment> sorted = assignmentRepository.sortByModuleTitle();

        assertEquals(5, sorted.size());

        for (int i = 0; i < sorted.size() - 1; i++) {
            String currentTitle = sorted.get(i).module().title();
            String nextTitle = sorted.get(i + 1).module().title();
            assertTrue(currentTitle.compareTo(nextTitle) <= 0);
        }
    }

    @Test
    @DisplayName("sortByMarkPointsDate should sort by mark, points desc, then date")
    void testSortByMarkPointsDate() {
        List<Assignment> sorted = assignmentRepository.sortByMarkPointsDate();

        assertEquals(5, sorted.size());

        for (int i = 0; i < sorted.size() - 1; i++) {
            Mark currentMark = sorted.get(i).mark();
            Mark nextMark = sorted.get(i + 1).mark();

            if (currentMark.equals(nextMark)) {
                int currentPoints = sorted.get(i).maxPoints();
                int nextPoints = sorted.get(i + 1).maxPoints();
                assertTrue(currentPoints >= nextPoints);
            }
        }
    }

    @Test
    @DisplayName("sortNaturally should use Comparable (dueDate)")
    void testSortNaturally() {
        List<Assignment> sorted = assignmentRepository.sortNaturally();

        assertEquals(5, sorted.size());

        for (int i = 0; i < sorted.size() - 1; i++) {
            LocalDate currentDate = sorted.get(i).dueDate();
            LocalDate nextDate = sorted.get(i + 1).dueDate();
            assertTrue(currentDate.isBefore(nextDate) || currentDate.equals(nextDate));
        }
    }

    @Test
    @DisplayName("sorting should not modify the original repository")
    void testSortingDoesNotModifyRepository() {
        List<Assignment> originalOrder = assignmentRepository.getAll();

        assignmentRepository.sortByDueDate();
        assignmentRepository.sortByMaxPoints();
        assignmentRepository.sortByMark();

        List<Assignment> currentOrder = assignmentRepository.getAll();

        assertEquals(originalOrder, currentOrder);
    }

    @Test
    @DisplayName("sorting empty repository should return empty list")
    void testSortingEmptyRepository() {
        AssignmentRepository emptyRepo = new AssignmentRepository();

        assertTrue(emptyRepo.sortByDueDate().isEmpty());
        assertTrue(emptyRepo.sortByMaxPoints().isEmpty());
        assertTrue(emptyRepo.sortByMark().isEmpty());
        assertTrue(emptyRepo.sortNaturally().isEmpty());
    }

    @Test
    @DisplayName("sorting single assignment should return list with that assignment")
    void testSortingSingleAssignment() {
        AssignmentRepository singleRepo = new AssignmentRepository();
        Assignment assignment = new Assignment(module1, LocalDate.of(2025, 6, 1), 100, Mark.EXCELLENT);
        singleRepo.add(assignment);

        List<Assignment> sortedByDate = singleRepo.sortByDueDate();
        assertEquals(1, sortedByDate.size());
        assertEquals(assignment, sortedByDate.get(0));

        List<Assignment> sortedByPoints = singleRepo.sortByMaxPoints();
        assertEquals(1, sortedByPoints.size());
        assertEquals(assignment, sortedByPoints.get(0));
    }

    @Test
    @DisplayName("sortByMaxPoints with same points should maintain order")
    void testSortByMaxPointsWithDuplicates() {
        AssignmentRepository repo = new AssignmentRepository();

        Assignment a1 = new Assignment(module1, LocalDate.of(2025, 6, 1), 85, Mark.GOOD);
        Assignment a2 = new Assignment(module2, LocalDate.of(2025, 6, 2), 85, Mark.EXCELLENT);
        Assignment a3 = new Assignment(module3, LocalDate.of(2025, 6, 3), 90, Mark.PASSED);

        repo.add(a1);
        repo.add(a2);
        repo.add(a3);

        List<Assignment> sorted = repo.sortByMaxPoints();

        assertEquals(3, sorted.size());
        assertEquals(90, sorted.get(0).maxPoints());
        assertEquals(85, sorted.get(1).maxPoints());
        assertEquals(85, sorted.get(2).maxPoints());
    }

    @Test
    @DisplayName("sortByModuleAndDate should group by module correctly")
    void testSortByModuleAndDateGrouping() {
        AssignmentRepository repo = new AssignmentRepository();

        myModule testModule = new myModule("Test", "Content");

        Assignment a1 = new Assignment(testModule, LocalDate.of(2025, 6, 10), 100, Mark.EXCELLENT);
        Assignment a2 = new Assignment(testModule, LocalDate.of(2025, 6, 5), 90, Mark.GOOD);
        Assignment a3 = new Assignment(testModule, LocalDate.of(2025, 6, 15), 80, Mark.PASSED);

        repo.add(a1);
        repo.add(a2);
        repo.add(a3);

        List<Assignment> sorted = repo.sortByModuleAndDate();

        assertEquals(3, sorted.size());
        assertEquals(LocalDate.of(2025, 6, 5), sorted.get(0).dueDate());
        assertEquals(LocalDate.of(2025, 6, 10), sorted.get(1).dueDate());
        assertEquals(LocalDate.of(2025, 6, 15), sorted.get(2).dueDate());
    }

    @Test
    @DisplayName("findByMark should work after sorting")
    void testFindAfterSort() {
        assignmentRepository.sortByMaxPoints();

        List<Assignment> found = assignmentRepository.findByMark(Mark.EXCELLENT);

        assertEquals(1, found.size());
        assertEquals(100, found.get(0).maxPoints());
    }

    @Test
    @DisplayName("sortByMarkPointsDate should handle complex ordering")
    void testSortByMarkPointsDateComplex() {
        AssignmentRepository repo = new AssignmentRepository();

        myModule testModule = new myModule("Test", "Content");

        Assignment a1 = new Assignment(testModule, LocalDate.of(2025, 6, 1), 100, Mark.GOOD);
        Assignment a2 = new Assignment(testModule, LocalDate.of(2025, 6, 2), 90, Mark.GOOD);
        Assignment a3 = new Assignment(testModule, LocalDate.of(2025, 6, 3), 100, Mark.EXCELLENT);

        repo.add(a2);
        repo.add(a3);
        repo.add(a1);

        List<Assignment> sorted = repo.sortByMarkPointsDate();

        assertEquals(3, sorted.size());
        assertTrue(sorted.get(0).mark().compareTo(sorted.get(1).mark()) <= 0);
    }
}