package ua.onlinecourses.parallel;

import ua.onlinecourses.model.Course;
import ua.onlinecourses.model.Instructor;
import ua.onlinecourses.model.Student;
import ua.onlinecourses.repository.CourseRepository;
import ua.onlinecourses.repository.InstructorRepository;
import ua.onlinecourses.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecutorServiceProcessor {
    private static final Logger logger = Logger.getLogger(ExecutorServiceProcessor.class.getName());

    private final ExecutorService executorService;

    public ExecutorServiceProcessor(int threadPoolSize) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        logger.log(Level.INFO, "ExecutorServiceProcessor created with {0} threads", threadPoolSize);
    }

    public void executeMultipleTasks(StudentRepository studentRepo,
                                     CourseRepository courseRepo,
                                     InstructorRepository instructorRepo) throws InterruptedException {
        logger.log(Level.INFO, "Starting multiple task execution");
        long startTime = System.currentTimeMillis();

        List<Callable<Object>> tasks = new ArrayList<>();

        tasks.add(() -> {
            new DataProcessingService.PrintStudentsRunnable(studentRepo).run();
            return null;
        });

        tasks.add(() -> new DataProcessingService.FilterStudentsByDomainTask(studentRepo, "chnu.edu.ua").call());
        tasks.add(() -> new DataProcessingService.CountCoursesByCreditsTask(courseRepo, 3).call());
        tasks.add(() -> new DataProcessingService.FindInstructorsByExpertiseTask(instructorRepo, 5).call());
        tasks.add(() -> new DataProcessingService.CalculateTotalCreditsTask(courseRepo).call());
        tasks.add(() -> new DataProcessingService.CalculateAverageExpertiseTask(instructorRepo).call());

        List<Future<Object>> futures = executorService.invokeAll(tasks);

        logger.log(Level.INFO, "All tasks submitted. Processing results...");

        int index = 0;
        for (Future<Object> future : futures) {
            try {
                Object result = future.get(30, TimeUnit.SECONDS);
                if (result != null) {
                    logger.log(Level.INFO, "Task {0} result: {1}", new Object[]{index, result});
                }
            } catch (ExecutionException e) {
                logger.log(Level.SEVERE, "Task {0} failed: {1}", new Object[]{index, e.getCause().getMessage()});
            } catch (TimeoutException e) {
                logger.log(Level.WARNING, "Task {0} timed out", index);
                future.cancel(true);
            }
            index++;
        }

        long endTime = System.currentTimeMillis();
        logger.log(Level.INFO, "Multiple task execution completed in {0} ms", (endTime - startTime));
    }

    public <T> Future<T> submitTask(Callable<T> task) {
        logger.log(Level.INFO, "Submitting single task for execution");
        return executorService.submit(task);
    }

    public void submitRunnable(Runnable task) {
        logger.log(Level.INFO, "Submitting runnable task for execution");
        executorService.submit(task);
    }

    public long filterCoursesWithExecutorService(CourseRepository repository, int minCredits) {
        logger.log(Level.INFO, "Filtering courses with ExecutorService approach");
        long startTime = System.currentTimeMillis();

        Callable<Long> task = new DataProcessingService.CountCoursesByCreditsTask(repository, minCredits);

        try {
            Future<Long> future = executorService.submit(task);
            Long result = future.get(30, TimeUnit.SECONDS);
            long endTime = System.currentTimeMillis();
            logger.log(Level.INFO, "ExecutorService filtering completed in {0} ms. Result: {1}",
                    new Object[]{(endTime - startTime), result});
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during ExecutorService filtering: {0}", e.getMessage());
            return 0;
        }
    }

    public void shutdown() {
        logger.log(Level.INFO, "Shutting down ExecutorServiceProcessor");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
