package ua.onlinecourses.parallel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.onlinecourses.model.Course;
import ua.onlinecourses.model.Instructor;
import ua.onlinecourses.model.Student;
import ua.onlinecourses.repository.CourseRepository;
import ua.onlinecourses.repository.InstructorRepository;
import ua.onlinecourses.repository.StudentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class DataProcessingTest {

    private StudentRepository studentRepo;
    private CourseRepository courseRepo;
    private InstructorRepository instructorRepo;

    @BeforeEach
    void setUp() {
        studentRepo = new StudentRepository();
        courseRepo = new CourseRepository();
        instructorRepo = new InstructorRepository();

        studentRepo.add(new Student("Lesia", "Melnyk", "lesia.melnyk@chnu.edu.ua", LocalDate.of(2023, 1, 1)));
        studentRepo.add(new Student("Liliya", "Fivko", "liliya.fivko@student.ua", LocalDate.of(2023, 2, 1)));
        studentRepo.add(new Student("Ivan", "Bondaryk", "ivan.bondaryk@chnu.edu.ua", LocalDate.of(2023, 3, 1)));

        courseRepo.add(new Course("Java Basics", "Introduction to Java", 5, LocalDate.now().plusMonths(1)));
        courseRepo.add(new Course("Python Intro", "Learn Python basics", 3, LocalDate.now().plusMonths(2)));
        courseRepo.add(new Course("Web Development", "HTML CSS JavaScript", 4, LocalDate.now().plusMonths(3)));

        instructorRepo.add(new Instructor("Igor", "Bylat", 15));
        instructorRepo.add(new Instructor("Denys", "Malyk", 25));
        instructorRepo.add(new Instructor("Inessa", "Kir", 10));
    }

    @Test
    void testFilterStudentsByDomainCallable() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        DataProcessingService.FilterStudentsByDomainTask task =
                new DataProcessingService.FilterStudentsByDomainTask(studentRepo, "chnu.edu.ua");

        Future<List<Student>> future = executor.submit(task);
        List<Student> result = future.get(10, TimeUnit.SECONDS);

        assertEquals(2, result.size(), "Should find 2 students with chnu.edu.ua domain");

        executor.shutdown();
    }

    @Test
    void testCountCoursesByCreditsCallable() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        DataProcessingService.CountCoursesByCreditsTask task =
                new DataProcessingService.CountCoursesByCreditsTask(courseRepo, 4);

        Future<Long> future = executor.submit(task);
        Long result = future.get(10, TimeUnit.SECONDS);

        assertEquals(2L, result, "Should find 2 courses with credits >= 4");

        executor.shutdown();
    }

    @Test
    void testFindInstructorsByExpertiseCallable() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        DataProcessingService.FindInstructorsByExpertiseTask task =
                new DataProcessingService.FindInstructorsByExpertiseTask(instructorRepo, 15);

        Future<List<Instructor>> future = executor.submit(task);
        List<Instructor> result = future.get(10, TimeUnit.SECONDS);

        assertEquals(2, result.size(), "Should find 2 instructors with expertise >= 15");

        executor.shutdown();
    }

    @Test
    void testCalculateTotalCreditsCallable() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        DataProcessingService.CalculateTotalCreditsTask task =
                new DataProcessingService.CalculateTotalCreditsTask(courseRepo);

        Future<Integer> future = executor.submit(task);
        Integer result = future.get(10, TimeUnit.SECONDS);

        assertEquals(12, result, "Total credits should be 12");

        executor.shutdown();
    }

    @Test
    void testCalculateAverageExpertiseCallable() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        DataProcessingService.CalculateAverageExpertiseTask task =
                new DataProcessingService.CalculateAverageExpertiseTask(instructorRepo);

        Future<Double> future = executor.submit(task);
        Double result = future.get(10, TimeUnit.SECONDS);

        assertEquals(50.0 / 3.0, result, 0.01, "Average expertise should be about 16.67");

        executor.shutdown();
    }

    @Test
    void testExecutorServiceProcessor() throws Exception {
        ExecutorServiceProcessor processor = new ExecutorServiceProcessor(4);

        processor.executeMultipleTasks(studentRepo, courseRepo, instructorRepo);

        processor.shutdown();
    }

    @Test
    void testCompletableFutureProcessing() throws Exception {
        CompletableFutureProcessor processor = new CompletableFutureProcessor(4);

        CompletableFuture<CompletableFutureProcessor.DataProcessingResult> resultFuture =
                processor.processAllDataAsync(studentRepo, courseRepo, instructorRepo);

        CompletableFutureProcessor.DataProcessingResult result = resultFuture.get(30, TimeUnit.SECONDS);

        assertEquals(3, result.studentCount(), "Should have 3 students");
        assertEquals(12, result.totalCredits(), "Total credits should be 12");
        assertEquals(3, result.courseTitles().size(), "Should have 3 course titles");

        processor.shutdown();
    }

    @Test
    void testCombineStudentFiltering() throws Exception {
        CompletableFutureProcessor processor = new CompletableFutureProcessor(4);

        CompletableFuture<List<Student>> resultFuture =
                processor.combineStudentFiltering(studentRepo, "chnu.edu.ua", "student.ua");

        List<Student> result = resultFuture.get(30, TimeUnit.SECONDS);

        assertEquals(3, result.size(), "Should find 3 students from both domains");

        processor.shutdown();
    }

    @Test
    void testProcessingComparisonResults() {
        long parallelResult = ProcessingComparison.filterWithParallelStream(courseRepo, 3);
        long sequentialResult = ProcessingComparison.filterWithSequentialStream(courseRepo, 3);

        assertEquals(sequentialResult, parallelResult,
                "Parallel and sequential filtering should produce same results");
    }

    @Test
    void testSumCreditsComparison() throws Exception {
        int parallelSum = ProcessingComparison.sumCreditsWithParallelStream(courseRepo);
        int completableFutureSum = ProcessingComparison.sumCreditsWithCompletableFuture(courseRepo)
                .get(10, TimeUnit.SECONDS);

        assertEquals(parallelSum, completableFutureSum,
                "Both approaches should produce same sum");
        assertEquals(12, parallelSum, "Sum should be 12");
    }
}
