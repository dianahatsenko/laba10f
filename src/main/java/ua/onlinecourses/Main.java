package ua.onlinecourses;

import ua.onlinecourses.config.AppConfig;
import ua.onlinecourses.exception.DataSerializationException;
import ua.onlinecourses.exception.InvalidDataException;
import ua.onlinecourses.model.*;
import ua.onlinecourses.parallel.CompletableFutureProcessor;
import ua.onlinecourses.parallel.ExecutorServiceProcessor;
import ua.onlinecourses.parallel.ParallelDataLoader;
import ua.onlinecourses.parallel.ProcessingComparison;
import ua.onlinecourses.persistence.PersistenceManager;
import ua.onlinecourses.repository.CourseRepository;
import ua.onlinecourses.repository.InstructorRepository;
import ua.onlinecourses.repository.ModuleRepository;
import ua.onlinecourses.repository.StudentRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        try {
            AppConfig config = new AppConfig();
            PersistenceManager manager = new PersistenceManager(config);


            StudentRepository studentRepo = new StudentRepository();
            CourseRepository courseRepo = new CourseRepository();
            InstructorRepository instructorRepo = new InstructorRepository();
            ModuleRepository moduleRepo = new ModuleRepository();

            logger.log(Level.INFO, "Parallel Data Loading from CSV Files");
            demonstrateParallelLoading(studentRepo, courseRepo, instructorRepo, moduleRepo);

            logger.log(Level.INFO, "");
            logger.log(Level.INFO, "Data Processing with ExecutorService");
            demonstrateExecutorServiceProcessing(studentRepo, courseRepo, instructorRepo);

            logger.log(Level.INFO, "");
            logger.log(Level.INFO, " Async Processing with CompletableFuture");
            demonstrateCompletableFutureProcessing(studentRepo, courseRepo, instructorRepo);

            logger.log(Level.INFO, "");
            logger.log(Level.INFO, "Comparison of Processing Approaches");
            demonstrateProcessingComparison(courseRepo);

            logger.log(Level.INFO, "");
            logger.log(Level.INFO, "Manual Repository Operations with Validation");
            demonstrateRepositoryWithValidation(config, manager);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in main: " + e.getMessage(), e);
        }
    }

    private static void demonstrateParallelLoading(StudentRepository studentRepo,
                                                    CourseRepository courseRepo,
                                                    InstructorRepository instructorRepo,
                                                    ModuleRepository moduleRepo) {
        logger.log(Level.INFO, "Loading all data from CSV files in parallel...");

        ParallelDataLoader loader = new ParallelDataLoader(studentRepo, courseRepo, instructorRepo, moduleRepo);

        try {
            CompletableFuture<Void> loadingFuture = loader.loadAllDataParallel(
                    "students.csv",
                    "courses.csv",
                    "instructors.csv",
                    "modules.csv"
            );

            loadingFuture.join();

            logger.log(Level.INFO, "Final repository sizes after parallel loading:");
            logger.log(Level.INFO, "  Students: {0}", studentRepo.size());
            logger.log(Level.INFO, "  Courses: {0}", courseRepo.size());
            logger.log(Level.INFO, "  Instructors: {0}", instructorRepo.size());
            logger.log(Level.INFO, "  Modules: {0}", moduleRepo.size());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during parallel loading: {0}", e.getMessage());
        } finally {
            loader.shutdown();
        }
    }

    private static void demonstrateExecutorServiceProcessing(StudentRepository studentRepo,
                                                              CourseRepository courseRepo,
                                                              InstructorRepository instructorRepo) {
        logger.log(Level.INFO, "Demonstrating data processing with ExecutorService...");

        ExecutorServiceProcessor processor = new ExecutorServiceProcessor(4);

        try {
            processor.executeMultipleTasks(studentRepo, courseRepo, instructorRepo);

            logger.log(Level.INFO, "ExecutorService processing completed successfully");

        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "ExecutorService processing interrupted: {0}", e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            processor.shutdown();
        }
    }

    private static void demonstrateCompletableFutureProcessing(StudentRepository studentRepo,
                                                                CourseRepository courseRepo,
                                                                InstructorRepository instructorRepo) {
        logger.log(Level.INFO, "Demonstrating async processing with CompletableFuture...");

        CompletableFutureProcessor processor = new CompletableFutureProcessor(4);

        try {
            CompletableFuture<CompletableFutureProcessor.DataProcessingResult> resultFuture =
                    processor.processAllDataAsync(studentRepo, courseRepo, instructorRepo);

            CompletableFutureProcessor.DataProcessingResult result = resultFuture.join();
            logger.log(Level.INFO, "Async processing result: {0}", result);

            CompletableFuture<List<Student>> combinedStudentsFuture =
                    processor.combineStudentFiltering(studentRepo, "chnu.edu.ua", "student.ua");

            List<Student> combinedStudents = combinedStudentsFuture.join();
            logger.log(Level.INFO, "Combined filtered students: {0}", combinedStudents.size());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "CompletableFuture processing error: {0}", e.getMessage());
        } finally {
            processor.shutdown();
        }
    }

    private static void demonstrateProcessingComparison(CourseRepository courseRepo) {
        logger.log(Level.INFO, "Comparing different processing approaches...");

        ProcessingComparison.compareFilteringApproaches(courseRepo, 3);

        logger.log(Level.INFO, "");

        ProcessingComparison.compareSumApproaches(courseRepo);
    }

    private static void demonstrateRepositoryWithValidation(AppConfig config, PersistenceManager manager)
            throws DataSerializationException {
        logger.log(Level.INFO, "Demonstrating manual repository operations with validation...");

        StudentRepository studentRepo = new StudentRepository();
        CourseRepository courseRepo = new CourseRepository();
        InstructorRepository instructorRepo = new InstructorRepository();

        List<Student> validStudents = new ArrayList<>();
        validStudents.add(new Student("Lesia", "Melnyk", "lesia.melnyk@chnu.edu.ua", LocalDate.of(2023, 9, 1)));
        validStudents.add(new Student("Liliya", "Fivko", "liliya.fivko@student.ua", LocalDate.of(2023, 9, 5)));
        validStudents.add(new Student("Ivan", "Bondaryk", "ivan.bondaryk@chnu.edu.ua", LocalDate.of(2023, 9, 3)));

        for (Student student : validStudents) {
            studentRepo.add(student);
        }
        logger.log(Level.INFO, "Added {0} valid students to repository", studentRepo.size());

        List<Course> validCourses = new ArrayList<>();
        validCourses.add(new Course("Java Programming", "Java Basics", 5, LocalDate.now().plusMonths(1)));
        validCourses.add(new Course("Data Structures", "Algorithms", 3, LocalDate.now().plusMonths(2)));
        validCourses.add(new Course("Web Development", "HTML and CSS", 4, LocalDate.now().plusMonths(3)));

        for (Course course : validCourses) {
            courseRepo.add(course);
        }
        logger.log(Level.INFO, "Added {0} valid courses to repository", courseRepo.size());

        List<Instructor> validInstructors = new ArrayList<>();
        validInstructors.add(new Instructor("Igor", "Bylat", 34));
        validInstructors.add(new Instructor("Denys", "Malyk", 20));
        validInstructors.add(new Instructor("Inessa", "Kir", 39));

        for (Instructor instructor : validInstructors) {
            instructorRepo.add(instructor);
        }
        logger.log(Level.INFO, "Added {0} valid instructors to repository", instructorRepo.size());

        logger.log(Level.INFO, "");
        logger.log(Level.INFO, "Saving validated objects to files...");
        manager.save(studentRepo.getAll(), "students", Student.class, "JSON");
        manager.save(courseRepo.getAll(), "courses", Course.class, "JSON");
        manager.save(instructorRepo.getAll(), "instructors", Instructor.class, "JSON");
        logger.log(Level.INFO, "All objects saved successfully!");

        logger.log(Level.INFO, "");
    }
}
