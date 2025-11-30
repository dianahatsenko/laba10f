package ua.onlinecourses.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.onlinecourses.model.myModule;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ModuleRepository Sorting Tests")
class ModuleRepositorySortingTest {

    private ModuleRepository moduleRepository;
    private myModule module1;
    private myModule module2;
    private myModule module3;
    private myModule module4;
    private myModule module5;

    @BeforeEach
    void setUp() {
        moduleRepository = new ModuleRepository();

        module1 = new myModule("Вступ до Java", "Основні концепції ООП та синтаксис мови");
        module2 = new myModule("Колекції", "List, Set, Map");
        module3 = new myModule("Потоки вводу-виводу", "IO Streams та робота з файлами");
        module4 = new myModule("Багатопоточність", "Threads and Concurrency");
        module5 = new myModule("Анотації", "Custom Annotations");

        moduleRepository.add(module1);
        moduleRepository.add(module2);
        moduleRepository.add(module3);
        moduleRepository.add(module4);
        moduleRepository.add(module5);
    }

    @Test
    @DisplayName("sortByTitle should sort modules by title")
    void testSortByTitle() {
        List<myModule> sorted = moduleRepository.sortByTitle();

        assertEquals(5, sorted.size());
        assertEquals("Анотації", sorted.get(0).title());
        assertEquals("Багатопоточність", sorted.get(1).title());
        assertEquals("Вступ до Java", sorted.get(2).title());
        assertEquals("Колекції", sorted.get(3).title());
        assertEquals("Потоки вводу-виводу", sorted.get(4).title());
    }

    @Test
    @DisplayName("sortByContent should sort modules by content")
    void testSortByContent() {
        List<myModule> sorted = moduleRepository.sortByContent();

        assertEquals(5, sorted.size());

        for (int i = 0; i < sorted.size() - 1; i++) {
            String current = sorted.get(i).content();
            String next = sorted.get(i + 1).content();
            assertTrue(current.compareTo(next) <= 0);
        }
    }

    @Test
    @DisplayName("sortByContentLength should sort by content length")
    void testSortByContentLength() {
        List<myModule> sorted = moduleRepository.sortByContentLength();

        assertEquals(5, sorted.size());

        for (int i = 0; i < sorted.size() - 1; i++) {
            int currentLength = sorted.get(i).content().length();
            int nextLength = sorted.get(i + 1).content().length();
            assertTrue(currentLength <= nextLength);
        }

        assertEquals("List, Set, Map", sorted.get(0).content());
    }

    @Test
    @DisplayName("sortByTitleLength should sort by title length")
    void testSortByTitleLength() {
        List<myModule> sorted = moduleRepository.sortByTitleLength();

        assertEquals(5, sorted.size());

        for (int i = 0; i < sorted.size() - 1; i++) {
            int currentLength = sorted.get(i).title().length();
            int nextLength = sorted.get(i + 1).title().length();
            assertTrue(currentLength <= nextLength);
        }
    }

    @Test
    @DisplayName("sortByTotalLength should sort by title + content length")
    void testSortByTotalLength() {
        List<myModule> sorted = moduleRepository.sortByTotalLength();

        assertEquals(5, sorted.size());

        for (int i = 0; i < sorted.size() - 1; i++) {
            int currentTotal = sorted.get(i).title().length() + sorted.get(i).content().length();
            int nextTotal = sorted.get(i + 1).title().length() + sorted.get(i + 1).content().length();
            assertTrue(currentTotal <= nextTotal);
        }
    }

    @Test
    @DisplayName("sortNaturally should use Comparable (fullName)")
    void testSortNaturally() {
        List<myModule> sorted = moduleRepository.sortNaturally();

        assertEquals(5, sorted.size());

        for (int i = 0; i < sorted.size() - 1; i++) {
            String currentFullName = sorted.get(i).getFullName();
            String nextFullName = sorted.get(i + 1).getFullName();
            assertTrue(currentFullName.compareTo(nextFullName) <= 0);
        }
    }

    @Test
    @DisplayName("sorting should not modify the original repository")
    void testSortingDoesNotModifyRepository() {
        List<myModule> originalOrder = moduleRepository.getAll();

        moduleRepository.sortByTitle();
        moduleRepository.sortByContent();
        moduleRepository.sortByContentLength();

        List<myModule> currentOrder = moduleRepository.getAll();

        assertEquals(originalOrder, currentOrder);
    }

    @Test
    @DisplayName("sorting empty repository should return empty list")
    void testSortingEmptyRepository() {
        ModuleRepository emptyRepo = new ModuleRepository();

        assertTrue(emptyRepo.sortByTitle().isEmpty());
        assertTrue(emptyRepo.sortByContent().isEmpty());
        assertTrue(emptyRepo.sortByContentLength().isEmpty());
        assertTrue(emptyRepo.sortNaturally().isEmpty());
    }

    @Test
    @DisplayName("sorting single module should return list with that module")
    void testSortingSingleModule() {
        ModuleRepository singleRepo = new ModuleRepository();
        myModule module = new myModule("Test", "Content");
        singleRepo.add(module);

        List<myModule> sortedByTitle = singleRepo.sortByTitle();
        assertEquals(1, sortedByTitle.size());
        assertEquals(module, sortedByTitle.get(0));

        List<myModule> sortedNaturally = singleRepo.sortNaturally();
        assertEquals(1, sortedNaturally.size());
        assertEquals(module, sortedNaturally.get(0));
    }

    @Test
    @DisplayName("sortByTitleLength with same length should sort by title")
    void testSortByTitleLengthWithSameLength() {
        ModuleRepository repo = new ModuleRepository();

        myModule m1 = new myModule("AAA", "Content1");
        myModule m2 = new myModule("BBB", "Content2");
        myModule m3 = new myModule("CCC", "Content3");

        repo.add(m3);
        repo.add(m1);
        repo.add(m2);

        List<myModule> sorted = repo.sortByTitleLength();

        assertEquals(3, sorted.size());
        assertEquals("AAA", sorted.get(0).title());
        assertEquals("BBB", sorted.get(1).title());
        assertEquals("CCC", sorted.get(2).title());
    }

    @Test
    @DisplayName("sortByTotalLength should handle modules with different lengths")
    void testSortByTotalLengthVariousLengths() {
        ModuleRepository repo = new ModuleRepository();

        myModule m1 = new myModule("AAA", "AAA");
        myModule m2 = new myModule("BBBB", "BBBB");
        myModule m3 = new myModule("CCCCC", "CCCCC");

        repo.add(m3);
        repo.add(m1);
        repo.add(m2);

        List<myModule> sorted = repo.sortByTotalLength();

        assertEquals(3, sorted.size());
        assertEquals(6, sorted.get(0).title().length() + sorted.get(0).content().length());
        assertEquals(8, sorted.get(1).title().length() + sorted.get(1).content().length());
        assertEquals(10, sorted.get(2).title().length() + sorted.get(2).content().length());
    }

    @Test
    @DisplayName("findByTitleContaining should work after sorting")
    void testFindAfterSort() {
        moduleRepository.sortByTitle();

        List<myModule> found = moduleRepository.findByTitleContaining("Java");

        assertEquals(1, found.size());
        assertEquals("Вступ до Java", found.get(0).title());
    }

    @Test
    @DisplayName("sortByContent should handle cyrillic and latin characters")
    void testSortByContentWithMixedCharacters() {
        ModuleRepository repo = new ModuleRepository();

        myModule m1 = new myModule("Title1", "ABC");
        myModule m2 = new myModule("Title2", "Алгоритми");
        myModule m3 = new myModule("Title3", "XYZ");

        repo.add(m2);
        repo.add(m3);
        repo.add(m1);

        List<myModule> sorted = repo.sortByContent();

        assertEquals(3, sorted.size());
    }
}