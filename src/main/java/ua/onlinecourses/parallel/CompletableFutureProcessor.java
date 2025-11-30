package ua.onlinecourses.parallel;

import ua.onlinecourses.model.Course;
import ua.onlinecourses.model.Instructor;
import ua.onlinecourses.model.Student;
import ua.onlinecourses.repository.CourseRepository;
import ua.onlinecourses.repository.InstructorRepository;
import ua.onlinecourses.repository.StudentRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CompletableFutureProcessor {
    private static final Logger logger = Logger.getLogger(CompletableFutureProcessor.class.getName());

    private final ExecutorService executorService;

    public CompletableFutureProcessor(int threadPoolSize) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        logger.log(Level.INFO, "CompletableFutureProcessor created with {0} threads", threadPoolSize);
    }

    public CompletableFuture<DataProcessingResult> processAllDataAsync(StudentRepository studentRepo,
                                                                        CourseRepository courseRepo,
                                                                        InstructorRepository instructorRepo) {
        logger.log(Level.INFO, "Starting async data processing with CompletableFuture");
        long startTime = System.currentTimeMillis();

        CompletableFuture<Long> studentCountFuture = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] counting students", threadName);
            return (long) studentRepo.size();
        }, executorService);

        CompletableFuture<Integer> totalCreditsFuture = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] calculating total credits", threadName);
            return courseRepo.getTotalCredits();
        }, executorService);

        CompletableFuture<Double> avgExpertiseFuture = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] calculating average expertise", threadName);
            return instructorRepo.getAverageExpertise();
        }, executorService);

        CompletableFuture<List<String>> courseTitlesFuture = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] getting course titles", threadName);
            return courseRepo.getAllTitles();
        }, executorService);

        return CompletableFuture.allOf(studentCountFuture, totalCreditsFuture, avgExpertiseFuture, courseTitlesFuture)
                .thenApply(v -> {
                    try {
                        DataProcessingResult result = new DataProcessingResult(
                                studentCountFuture.get(),
                                totalCreditsFuture.get(),
                                avgExpertiseFuture.get(),
                                courseTitlesFuture.get()
                        );
                        long endTime = System.currentTimeMillis();
                        logger.log(Level.INFO, "Async processing completed in {0} ms", (endTime - startTime));
                        return result;
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error combining results: {0}", e.getMessage());
                        return new DataProcessingResult(0, 0, 0.0, List.of());
                    }
                })
                .handle((result, ex) -> {
                    if (ex != null) {
                        logger.log(Level.SEVERE, "Error in async processing: {0}", ex.getMessage());
                        return new DataProcessingResult(0, 0, 0.0, List.of());
                    }
                    return result;
                });
    }

    public CompletableFuture<Long> filterCoursesWithCompletableFuture(CourseRepository repository, int minCredits) {
        logger.log(Level.INFO, "Filtering courses with CompletableFuture approach");
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] filtering courses with credits >= {1}",
                    new Object[]{threadName, minCredits});
            try {
                long count = repository.getAll().stream()
                        .filter(c -> c.credits() >= minCredits)
                        .count();
                long endTime = System.currentTimeMillis();
                logger.log(Level.INFO, "CompletableFuture filtering completed in {0} ms. Result: {1}",
                        new Object[]{(endTime - startTime), count});
                return count;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Thread [{0}] error filtering courses: {1}",
                        new Object[]{threadName, e.getMessage()});
                throw e;
            }
        }, executorService)
        .handle((result, ex) -> {
            if (ex != null) {
                logger.log(Level.SEVERE, "Error in CompletableFuture filtering: {0}", ex.getMessage());
                return 0L;
            }
            return result;
        });
    }

    public CompletableFuture<List<Student>> combineStudentFiltering(StudentRepository repository,
                                                                     String domain1, String domain2) {
        logger.log(Level.INFO, "Combining student filtering from two domains");

        CompletableFuture<List<Student>> domain1Future = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] filtering students by domain: {1}",
                    new Object[]{threadName, domain1});
            return repository.findByEmailDomain(domain1);
        }, executorService);

        CompletableFuture<List<Student>> domain2Future = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] filtering students by domain: {1}",
                    new Object[]{threadName, domain2});
            return repository.findByEmailDomain(domain2);
        }, executorService);

        return domain1Future.thenCombine(domain2Future, (list1, list2) -> {
            logger.log(Level.INFO, "Combining results: {0} + {1} students",
                    new Object[]{list1.size(), list2.size()});
            List<Student> combined = new java.util.ArrayList<>(list1);
            combined.addAll(list2);
            return combined;
        }).handle((result, ex) -> {
            if (ex != null) {
                logger.log(Level.SEVERE, "Error combining student filtering: {0}", ex.getMessage());
                return List.of();
            }
            return result;
        });
    }

    public void shutdown() {
        logger.log(Level.INFO, "Shutting down CompletableFutureProcessor");
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

    public record DataProcessingResult(
            long studentCount,
            int totalCredits,
            double averageExpertise,
            List<String> courseTitles
    ) {
        @Override
        public String toString() {
            return String.format("DataProcessingResult{students=%d, totalCredits=%d, avgExpertise=%.2f, courses=%s}",
                    studentCount, totalCredits, averageExpertise, courseTitles);
        }
    }
}
