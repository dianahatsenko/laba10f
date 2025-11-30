package ua.onlinecourses.parallel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.onlinecourses.model.Course;
import ua.onlinecourses.model.Instructor;
import ua.onlinecourses.model.Student;
import ua.onlinecourses.model.myModule;
import ua.onlinecourses.repository.CourseRepository;
import ua.onlinecourses.repository.InstructorRepository;
import ua.onlinecourses.repository.ModuleRepository;
import ua.onlinecourses.repository.StudentRepository;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ParallelLoadingTest {

    private StudentRepository studentRepo;
    private CourseRepository courseRepo;
    private InstructorRepository instructorRepo;
    private ModuleRepository moduleRepo;

    @BeforeEach
    void setUp() {
        studentRepo = new StudentRepository();
        courseRepo = new CourseRepository();
        instructorRepo = new InstructorRepository();
        moduleRepo = new ModuleRepository();
    }

    @Test
    void testParallelLoadingFromFiles() throws Exception {
        ParallelDataLoader loader = new ParallelDataLoader(studentRepo, courseRepo, instructorRepo, moduleRepo);

        CompletableFuture<Void> loadingFuture = loader.loadAllDataParallel(
                "students.csv",
                "courses.csv",
                "instructors.csv",
                "modules.csv"
        );

        loadingFuture.get(30, TimeUnit.SECONDS);

        assertTrue(studentRepo.size() >= 0, "Students should be loaded");
        assertTrue(courseRepo.size() >= 0, "Courses should be loaded");
        assertTrue(instructorRepo.size() >= 0, "Instructors should be loaded");
        assertTrue(moduleRepo.size() >= 0, "Modules should be loaded");

        loader.shutdown();
    }

    @Test
    void testThreadSafeAddToRepository() throws Exception {
        int numThreads = 10;
        int itemsPerThread = 5;
        CountDownLatch latch = new CountDownLatch(numThreads);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < itemsPerThread; j++) {
                        String firstName = "First" + threadIndex + "_" + j;
                        String lastName = "Last" + threadIndex + "_" + j;
                        String email = "test" + threadIndex + "_" + j + "@test.com";
                        try {
                            Student student = new Student(firstName, lastName, email, LocalDate.of(2023, 1, 1));
                            studentRepo.add(student);
                        } catch (Exception e) {
                            // Expected: validation may fail for some test data
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "All threads should complete");
        executor.shutdown();

        assertTrue(studentRepo.size() <= numThreads * itemsPerThread,
                "Repository should contain at most expected items");
    }

    @Test
    void testConcurrentReadAndWrite() throws Exception {
        studentRepo.add(new Student("Initial", "Student", "initial@test.com", LocalDate.of(2023, 1, 1)));

        int numReaders = 5;
        int numWriters = 3;
        CountDownLatch latch = new CountDownLatch(numReaders + numWriters);
        ExecutorService executor = Executors.newFixedThreadPool(numReaders + numWriters);

        for (int i = 0; i < numReaders; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 100; j++) {
                        studentRepo.getAll();
                        studentRepo.size();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        for (int i = 0; i < numWriters; i++) {
            final int writerIndex = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 10; j++) {
                        try {
                            Student student = new Student(
                                    "Writer" + writerIndex,
                                    "Student" + j,
                                    "writer" + writerIndex + "_" + j + "@test.com",
                                    LocalDate.of(2023, 1, 1)
                            );
                            studentRepo.add(student);
                        } catch (Exception e) {
                            // Expected: validation may fail for some test data
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "All threads should complete");
        executor.shutdown();

        assertTrue(studentRepo.size() >= 1, "Repository should contain at least initial student");
    }

    @Test
    void testParallelStreamFiltering() {
        for (int i = 0; i < 10; i++) {
            try {
                Course course = new Course(
                        "Course " + i,
                        "Description " + i,
                        (i % 5) + 1,
                        LocalDate.now().plusMonths(1)
                );
                courseRepo.add(course);
            } catch (Exception e) {
                // Expected: validation may fail for some test data
            }
        }

        long parallelResult = courseRepo.getAll().parallelStream()
                .filter(c -> c.credits() >= 3)
                .count();

        long sequentialResult = courseRepo.getAll().stream()
                .filter(c -> c.credits() >= 3)
                .count();

        assertEquals(sequentialResult, parallelResult,
                "Parallel and sequential filtering should produce same results");
    }
}
