package ua.onlinecourses.servlet;

import ua.onlinecourses.config.AppConfig;
import ua.onlinecourses.model.Course;
import ua.onlinecourses.model.Instructor;
import ua.onlinecourses.model.Student;
import ua.onlinecourses.model.myModule;
import ua.onlinecourses.parallel.ParallelDataLoader;
import ua.onlinecourses.persistence.PersistenceManager;
import ua.onlinecourses.repository.CourseRepository;
import ua.onlinecourses.repository.InstructorRepository;
import ua.onlinecourses.repository.ModuleRepository;
import ua.onlinecourses.repository.StudentRepository;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepositoryManager {

    private static final Logger logger = Logger.getLogger(RepositoryManager.class.getName());

    private static RepositoryManager instance;

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final ModuleRepository moduleRepository;
    private final AppConfig config;
    private final PersistenceManager persistenceManager;

    private RepositoryManager() {
        this.studentRepository = new StudentRepository();
        this.courseRepository = new CourseRepository();
        this.instructorRepository = new InstructorRepository();
        this.moduleRepository = new ModuleRepository();
        this.config = new AppConfig();
        this.persistenceManager = new PersistenceManager(config);
        loadDataFromFiles();
    }

    public static synchronized RepositoryManager getInstance() {
        if (instance == null) {
            instance = new RepositoryManager();
        }
        return instance;
    }

    private void loadDataFromFiles() {
        logger.log(Level.INFO, "Loading data from CSV files...");
        ParallelDataLoader loader = new ParallelDataLoader(
                studentRepository, courseRepository, instructorRepository, moduleRepository);

        try {
            CompletableFuture<Void> loadingFuture = loader.loadAllDataParallel(
                    "students.csv", "courses.csv", "instructors.csv", "modules.csv");
            loadingFuture.join();

            logger.log(Level.INFO, "Data loaded: Students={0}, Courses={1}, Instructors={2}, Modules={3}",
                    new Object[]{studentRepository.size(), courseRepository.size(),
                            instructorRepository.size(), moduleRepository.size()});
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading data from CSV files: {0}", e.getMessage());
        } finally {
            loader.shutdown();
        }
    }

    public void saveStudentsToFile() {
        try {
            persistenceManager.save(studentRepository.getAll(), "students", Student.class, "JSON");
            logger.log(Level.INFO, "Students saved to file");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving students to file: {0}", e.getMessage());
        }
    }

    public void saveCoursesToFile() {
        try {
            persistenceManager.save(courseRepository.getAll(), "courses", Course.class, "JSON");
            logger.log(Level.INFO, "Courses saved to file");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving courses to file: {0}", e.getMessage());
        }
    }

    public void saveInstructorsToFile() {
        try {
            persistenceManager.save(instructorRepository.getAll(), "instructors", Instructor.class, "JSON");
            logger.log(Level.INFO, "Instructors saved to file");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving instructors to file: {0}", e.getMessage());
        }
    }

    public void saveModulesToFile() {
        try {
            persistenceManager.save(moduleRepository.getAll(), "modules", myModule.class, "JSON");
            logger.log(Level.INFO, "Modules saved to file");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving modules to file: {0}", e.getMessage());
        }
    }

    public StudentRepository getStudentRepository() {
        return studentRepository;
    }

    public CourseRepository getCourseRepository() {
        return courseRepository;
    }

    public InstructorRepository getInstructorRepository() {
        return instructorRepository;
    }

    public ModuleRepository getModuleRepository() {
        return moduleRepository;
    }

    public static synchronized void reset() {
        instance = null;
    }
}
