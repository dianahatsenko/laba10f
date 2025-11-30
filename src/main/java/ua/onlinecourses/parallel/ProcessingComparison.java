package ua.onlinecourses.parallel;

import ua.onlinecourses.model.Course;
import ua.onlinecourses.repository.CourseRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessingComparison {
    private static final Logger logger = Logger.getLogger(ProcessingComparison.class.getName());

    public static void compareFilteringApproaches(CourseRepository repository, int minCredits) {
        logger.log(Level.INFO, "=== Starting comparison of filtering approaches ===");
        logger.log(Level.INFO, "Filtering courses with credits >= {0}", minCredits);

        long parallelStreamResult = filterWithParallelStream(repository, minCredits);
        long sequentialStreamResult = filterWithSequentialStream(repository, minCredits);

        ExecutorServiceProcessor executorProcessor = new ExecutorServiceProcessor(4);
        long executorServiceResult = executorProcessor.filterCoursesWithExecutorService(repository, minCredits);

        CompletableFutureProcessor cfProcessor = new CompletableFutureProcessor(4);
        long completableFutureResult = 0;
        try {
            completableFutureResult = cfProcessor.filterCoursesWithCompletableFuture(repository, minCredits)
                    .get();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in CompletableFuture comparison: {0}", e.getMessage());
        }

        logger.log(Level.INFO, "=== Comparison Results ===");
        logger.log(Level.INFO, "Sequential Stream result: {0}", sequentialStreamResult);
        logger.log(Level.INFO, "Parallel Stream result: {0}", parallelStreamResult);
        logger.log(Level.INFO, "ExecutorService result: {0}", executorServiceResult);
        logger.log(Level.INFO, "CompletableFuture result: {0}", completableFutureResult);

        boolean allEqual = (parallelStreamResult == sequentialStreamResult) &&
                           (sequentialStreamResult == executorServiceResult) &&
                           (executorServiceResult == completableFutureResult);
        logger.log(Level.INFO, "All results equal: {0}", allEqual);

        executorProcessor.shutdown();
        cfProcessor.shutdown();
    }

    public static long filterWithParallelStream(CourseRepository repository, int minCredits) {
        String threadName = Thread.currentThread().getName();
        logger.log(Level.INFO, "Thread [{0}] starting Parallel Stream filtering", threadName);
        long startTime = System.currentTimeMillis();

        try {
            List<Course> allCourses = repository.getAll();
            long count = allCourses.parallelStream()
                    .filter(course -> course.credits() >= minCredits)
                    .count();

            long endTime = System.currentTimeMillis();
            logger.log(Level.INFO, "Parallel Stream filtering completed in {0} ms. Found {1} courses",
                    new Object[]{(endTime - startTime), count});
            return count;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in Parallel Stream filtering: {0}", e.getMessage());
            return 0;
        }
    }

    public static long filterWithSequentialStream(CourseRepository repository, int minCredits) {
        String threadName = Thread.currentThread().getName();
        logger.log(Level.INFO, "Thread [{0}] starting Sequential Stream filtering", threadName);
        long startTime = System.currentTimeMillis();

        try {
            List<Course> allCourses = repository.getAll();
            long count = allCourses.stream()
                    .filter(course -> course.credits() >= minCredits)
                    .count();

            long endTime = System.currentTimeMillis();
            logger.log(Level.INFO, "Sequential Stream filtering completed in {0} ms. Found {1} courses",
                    new Object[]{(endTime - startTime), count});
            return count;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in Sequential Stream filtering: {0}", e.getMessage());
            return 0;
        }
    }

    public static int sumCreditsWithParallelStream(CourseRepository repository) {
        String threadName = Thread.currentThread().getName();
        logger.log(Level.INFO, "Thread [{0}] starting Parallel Stream credits sum", threadName);
        long startTime = System.currentTimeMillis();

        try {
            List<Course> allCourses = repository.getAll();
            int sum = allCourses.parallelStream()
                    .mapToInt(Course::credits)
                    .sum();

            long endTime = System.currentTimeMillis();
            logger.log(Level.INFO, "Parallel Stream credits sum completed in {0} ms. Total: {1}",
                    new Object[]{(endTime - startTime), sum});
            return sum;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in Parallel Stream credits sum: {0}", e.getMessage());
            return 0;
        }
    }

    public static CompletableFuture<Integer> sumCreditsWithCompletableFuture(CourseRepository repository) {
        logger.log(Level.INFO, "Starting CompletableFuture credits sum");
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] calculating credits sum", threadName);
            List<Course> allCourses = repository.getAll();
            int sum = allCourses.stream()
                    .mapToInt(Course::credits)
                    .sum();
            long endTime = System.currentTimeMillis();
            logger.log(Level.INFO, "CompletableFuture credits sum completed in {0} ms. Total: {1}",
                    new Object[]{(endTime - startTime), sum});
            return sum;
        }).handle((result, ex) -> {
            if (ex != null) {
                logger.log(Level.SEVERE, "Error in CompletableFuture credits sum: {0}", ex.getMessage());
                return 0;
            }
            return result;
        });
    }

    public static void compareSumApproaches(CourseRepository repository) {
        logger.log(Level.INFO, "=== Starting comparison of sum approaches ===");

        int parallelResult = sumCreditsWithParallelStream(repository);

        int completableFutureResult = 0;
        try {
            completableFutureResult = sumCreditsWithCompletableFuture(repository).get();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting CompletableFuture result: {0}", e.getMessage());
        }

        logger.log(Level.INFO, "=== Sum Comparison Results ===");
        logger.log(Level.INFO, "Parallel Stream sum: {0}", parallelResult);
        logger.log(Level.INFO, "CompletableFuture sum: {0}", completableFutureResult);
        logger.log(Level.INFO, "Results equal: {0}", parallelResult == completableFutureResult);
    }
}
