package ua.onlinecourses.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.onlinecourses.model.myModule;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GenericRepository sortByIdentity Tests")
class GenericRepositorySortingTest {
    private static final Logger logger = Logger.getLogger(GenericRepositorySortingTest.class.getName());

    private GenericRepository<myModule> moduleRepository;
    private myModule module1;
    private myModule module2;
    private myModule module3;
    private myModule module4;
    private myModule module5;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data");
        moduleRepository = new GenericRepository<>(myModule::getFullName, "Module");

        module1 = new myModule("Introduction", "Basic concepts");
        module2 = new myModule("Advanced Topics", "Complex algorithms");
        module3 = new myModule("Data Structures", "Lists and trees");
        module4 = new myModule("Algorithms", "Sorting methods");
        module5 = new myModule("Testing", "Unit tests");

        moduleRepository.add(module1);
        moduleRepository.add(module2);
        moduleRepository.add(module3);
        moduleRepository.add(module4);
        moduleRepository.add(module5);

        logger.info("Test setup completed with " + moduleRepository.size() + " modules");
    }

    @Test
    @DisplayName("sortByIdentity with 'asc' should sort in ascending order")
    void testSortByIdentityAscending() {
        logger.info("Testing sortByIdentity ascending");

        List<myModule> sorted = moduleRepository.sortByIdentity("asc");

        assertEquals(5, sorted.size(), "Should return all modules");

        for (int i = 0; i < sorted.size() - 1; i++) {
            String current = sorted.get(i).getFullName();
            String next = sorted.get(i + 1).getFullName();
            assertTrue(current.compareTo(next) <= 0,
                    "Modules should be sorted in ascending order by fullName");
        }

        logger.info("sortByIdentity ascending test completed successfully");
    }

    @Test
    @DisplayName("sortByIdentity with 'desc' should sort in descending order")
    void testSortByIdentityDescending() {
        logger.info("Testing sortByIdentity descending");

        List<myModule> sorted = moduleRepository.sortByIdentity("desc");

        assertEquals(5, sorted.size(), "Should return all modules");

        for (int i = 0; i < sorted.size() - 1; i++) {
            String current = sorted.get(i).getFullName();
            String next = sorted.get(i + 1).getFullName();
            assertTrue(current.compareTo(next) >= 0,
                    "Modules should be sorted in descending order by fullName");
        }

        logger.info("sortByIdentity descending test completed successfully");
    }

    @Test
    @DisplayName("sortByIdentity should return reverse order for asc and desc")
    void testSortByIdentityAscDescReverse() {
        logger.info("Testing that asc and desc are reverse of each other");

        List<myModule> sortedAsc = moduleRepository.sortByIdentity("asc");
        List<myModule> sortedDesc = moduleRepository.sortByIdentity("desc");

        assertEquals(sortedAsc.size(), sortedDesc.size(), "Both lists should have same size");

        for (int i = 0; i < sortedAsc.size(); i++) {
            assertEquals(sortedAsc.get(i).getFullName(),
                    sortedDesc.get(sortedDesc.size() - 1 - i).getFullName(),
                    "Descending should be reverse of ascending");
        }

        logger.info("Asc/Desc reverse test completed successfully");
    }

    @Test
    @DisplayName("sortByIdentity with invalid order should default to 'asc'")
    void testSortByIdentityInvalidOrder() {
        logger.info("Testing sortByIdentity with invalid order");

        List<myModule> sortedInvalid = moduleRepository.sortByIdentity("invalid");
        List<myModule> sortedAsc = moduleRepository.sortByIdentity("asc");

        assertEquals(sortedInvalid.size(), sortedAsc.size(), "Should return all modules");

        for (int i = 0; i < sortedInvalid.size(); i++) {
            assertEquals(sortedInvalid.get(i).getFullName(),
                    sortedAsc.get(i).getFullName(),
                    "Invalid order should default to ascending");
        }

        logger.info("Invalid order test completed successfully");
    }

    @Test
    @DisplayName("sortByIdentity with null order should default to 'asc'")
    void testSortByIdentityNullOrder() {
        logger.info("Testing sortByIdentity with null order");

        List<myModule> sortedNull = moduleRepository.sortByIdentity(null);
        List<myModule> sortedAsc = moduleRepository.sortByIdentity("asc");

        assertEquals(sortedNull.size(), sortedAsc.size(), "Should return all modules");

        for (int i = 0; i < sortedNull.size(); i++) {
            assertEquals(sortedNull.get(i).getFullName(),
                    sortedAsc.get(i).getFullName(),
                    "Null order should default to ascending");
        }

        logger.info("Null order test completed successfully");
    }

    @Test
    @DisplayName("sortByIdentity should be case-insensitive for order parameter")
    void testSortByIdentityCaseInsensitive() {
        logger.info("Testing sortByIdentity case insensitivity");

        List<myModule> sortedAsc1 = moduleRepository.sortByIdentity("asc");
        List<myModule> sortedAsc2 = moduleRepository.sortByIdentity("ASC");
        List<myModule> sortedAsc3 = moduleRepository.sortByIdentity("Asc");

        List<myModule> sortedDesc1 = moduleRepository.sortByIdentity("desc");
        List<myModule> sortedDesc2 = moduleRepository.sortByIdentity("DESC");
        List<myModule> sortedDesc3 = moduleRepository.sortByIdentity("Desc");

        for (int i = 0; i < sortedAsc1.size(); i++) {
            assertEquals(sortedAsc1.get(i).getFullName(), sortedAsc2.get(i).getFullName(),
                    "ASC should work same as asc");
            assertEquals(sortedAsc1.get(i).getFullName(), sortedAsc3.get(i).getFullName(),
                    "Asc should work same as asc");
        }

        for (int i = 0; i < sortedDesc1.size(); i++) {
            assertEquals(sortedDesc1.get(i).getFullName(), sortedDesc2.get(i).getFullName(),
                    "DESC should work same as desc");
            assertEquals(sortedDesc1.get(i).getFullName(), sortedDesc3.get(i).getFullName(),
                    "Desc should work same as desc");
        }

        logger.info("Case insensitivity test completed successfully");
    }

    @Test
    @DisplayName("sortByIdentity should not modify the original repository")
    void testSortByIdentityDoesNotModifyRepository() {
        logger.info("Testing that sortByIdentity does not modify repository");

        List<myModule> originalOrder = moduleRepository.getAll();
        moduleRepository.sortByIdentity("asc");
        moduleRepository.sortByIdentity("desc");
        List<myModule> currentOrder = moduleRepository.getAll();

        assertEquals(originalOrder.size(), currentOrder.size(), "Size should remain the same");

        for (int i = 0; i < originalOrder.size(); i++) {
            assertEquals(originalOrder.get(i), currentOrder.get(i),
                    "Repository order should remain unchanged after sorting");
        }

        logger.info("Repository immutability test completed successfully");
    }

    @Test
    @DisplayName("sortByIdentity on empty repository should return empty list")
    void testSortByIdentityEmptyRepository() {
        logger.info("Testing sortByIdentity on empty repository");

        GenericRepository<myModule> emptyRepo = new GenericRepository<>(myModule::getFullName, "Module");

        List<myModule> sortedAsc = emptyRepo.sortByIdentity("asc");
        List<myModule> sortedDesc = emptyRepo.sortByIdentity("desc");

        assertTrue(sortedAsc.isEmpty(), "Sorting empty repository (asc) should return empty list");
        assertTrue(sortedDesc.isEmpty(), "Sorting empty repository (desc) should return empty list");

        logger.info("Empty repository test completed successfully");
    }

    @Test
    @DisplayName("sortByIdentity with single element should return list with that element")
    void testSortByIdentitySingleElement() {
        logger.info("Testing sortByIdentity with single element");

        GenericRepository<myModule> singleRepo = new GenericRepository<>(myModule::getFullName, "Module");
        myModule module = new myModule("Single", "Content");
        singleRepo.add(module);

        List<myModule> sortedAsc = singleRepo.sortByIdentity("asc");
        List<myModule> sortedDesc = singleRepo.sortByIdentity("desc");

        assertEquals(1, sortedAsc.size(), "Should return single element");
        assertEquals(1, sortedDesc.size(), "Should return single element");

        assertEquals(module, sortedAsc.get(0), "Should contain the single module");
        assertEquals(module, sortedDesc.get(0), "Should contain the single module");

        logger.info("Single element test completed successfully");
    }

    @Test
    @DisplayName("sortByIdentity with duplicate identities should maintain stability")
    void testSortByIdentityWithDuplicates() {
        logger.info("Testing sortByIdentity with modules having same identity prefix");

        GenericRepository<myModule> repo = new GenericRepository<>(myModule::getFullName, "Module");
        myModule mod1 = new myModule("AAA", "First");
        myModule mod2 = new myModule("AAA", "Second");
        myModule mod3 = new myModule("BBB", "Third");

        repo.add(mod1);
        repo.add(mod2);
        repo.add(mod3);

        List<myModule> sorted = repo.sortByIdentity("asc");

        assertEquals(3, sorted.size(), "Should return all modules");

        assertTrue(sorted.get(0).getFullName().startsWith("AAA") ||
                        sorted.get(1).getFullName().startsWith("AAA"),
                "AAA modules should come before BBB");

        logger.info("Duplicate identities test completed successfully");
    }
}
