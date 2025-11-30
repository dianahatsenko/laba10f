package ua.onlinecourses.parallel;

import ua.onlinecourses.model.Course;
import ua.onlinecourses.model.Instructor;
import ua.onlinecourses.model.Student;
import ua.onlinecourses.model.myModule;
import ua.onlinecourses.parser.CourseFileParser;
import ua.onlinecourses.parser.InstructorFileParser;
import ua.onlinecourses.parser.ModuleFileParser;
import ua.onlinecourses.parser.StudentFileParser;
import ua.onlinecourses.repository.CourseRepository;
import ua.onlinecourses.repository.InstructorRepository;
import ua.onlinecourses.repository.ModuleRepository;
import ua.onlinecourses.repository.StudentRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParallelDataLoader {
    private static final Logger logger = Logger.getLogger(ParallelDataLoader.class.getName());

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final ModuleRepository moduleRepository;
    private final ExecutorService executorService;

    public ParallelDataLoader(StudentRepository studentRepository,
                              CourseRepository courseRepository,
                              InstructorRepository instructorRepository,
                              ModuleRepository moduleRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
        this.moduleRepository = moduleRepository;
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public CompletableFuture<Void> loadAllDataParallel(String studentsFile,
                                                        String coursesFile,
                                                        String instructorsFile,
                                                        String modulesFile) {
        logger.log(Level.INFO, "Starting parallel data loading from files");
        long startTime = System.currentTimeMillis();

        CompletableFuture<Integer> studentsFuture = loadStudentsAsync(studentsFile);
        CompletableFuture<Integer> coursesFuture = loadCoursesAsync(coursesFile);
        CompletableFuture<Integer> instructorsFuture = loadInstructorsAsync(instructorsFile);
        CompletableFuture<Integer> modulesFuture = loadModulesAsync(modulesFile);

        return CompletableFuture.allOf(studentsFuture, coursesFuture, instructorsFuture, modulesFuture)
                .thenRun(() -> {
                    long endTime = System.currentTimeMillis();
                    logger.log(Level.INFO, "Parallel data loading completed in {0} ms", (endTime - startTime));
                    try {
                        logger.log(Level.INFO, "Loaded: {0} students, {1} courses, {2} instructors, {3} modules",
                                new Object[]{studentsFuture.get(), coursesFuture.get(),
                                        instructorsFuture.get(), modulesFuture.get()});
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Error getting loading results: {0}", e.getMessage());
                    }
                })
                .handle((result, ex) -> {
                    if (ex != null) {
                        logger.log(Level.SEVERE, "Error during parallel loading: {0}", ex.getMessage());
                    }
                    return null;
                });
    }

    private CompletableFuture<Integer> loadStudentsAsync(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] started loading students from {1}", new Object[]{threadName, filePath});
            try {
                List<Student> students = StudentFileParser.parseFromCSV(filePath);
                int count = 0;
                for (Student student : students) {
                    if (studentRepository.add(student)) {
                        count++;
                    }
                }
                logger.log(Level.INFO, "Thread [{0}] completed loading {1} students", new Object[]{threadName, count});
                return count;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Thread [{0}] error loading students: {1}", new Object[]{threadName, e.getMessage()});
                return 0;
            }
        }, executorService);
    }

    private CompletableFuture<Integer> loadCoursesAsync(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] started loading courses from {1}", new Object[]{threadName, filePath});
            try {
                List<Course> courses = CourseFileParser.parseFromCSV(filePath);
                int count = 0;
                for (Course course : courses) {
                    if (courseRepository.add(course)) {
                        count++;
                    }
                }
                logger.log(Level.INFO, "Thread [{0}] completed loading {1} courses", new Object[]{threadName, count});
                return count;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Thread [{0}] error loading courses: {1}", new Object[]{threadName, e.getMessage()});
                return 0;
            }
        }, executorService);
    }

    private CompletableFuture<Integer> loadInstructorsAsync(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] started loading instructors from {1}", new Object[]{threadName, filePath});
            try {
                List<Instructor> instructors = InstructorFileParser.parseFromCSV(filePath);
                int count = 0;
                for (Instructor instructor : instructors) {
                    if (instructorRepository.add(instructor)) {
                        count++;
                    }
                }
                logger.log(Level.INFO, "Thread [{0}] completed loading {1} instructors", new Object[]{threadName, count});
                return count;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Thread [{0}] error loading instructors: {1}", new Object[]{threadName, e.getMessage()});
                return 0;
            }
        }, executorService);
    }

    private CompletableFuture<Integer> loadModulesAsync(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] started loading modules from {1}", new Object[]{threadName, filePath});
            try {
                List<myModule> modules = ModuleFileParser.parseFromCSV(filePath);
                int count = 0;
                for (myModule module : modules) {
                    if (moduleRepository.add(module)) {
                        count++;
                    }
                }
                logger.log(Level.INFO, "Thread [{0}] completed loading {1} modules", new Object[]{threadName, count});
                return count;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Thread [{0}] error loading modules: {1}", new Object[]{threadName, e.getMessage()});
                return 0;
            }
        }, executorService);
    }

    public void shutdown() {
        logger.log(Level.INFO, "Shutting down ParallelDataLoader executor service");
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
