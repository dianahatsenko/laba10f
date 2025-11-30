package ua.onlinecourses.parallel;

import ua.onlinecourses.model.Course;
import ua.onlinecourses.model.Instructor;
import ua.onlinecourses.model.Student;
import ua.onlinecourses.repository.CourseRepository;
import ua.onlinecourses.repository.InstructorRepository;
import ua.onlinecourses.repository.StudentRepository;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DataProcessingService {
    private static final Logger logger = Logger.getLogger(DataProcessingService.class.getName());

    public static class FilterStudentsByDomainTask implements Callable<List<Student>> {
        private final StudentRepository repository;
        private final String domain;

        public FilterStudentsByDomainTask(StudentRepository repository, String domain) {
            this.repository = repository;
            this.domain = domain;
        }

        @Override
        public List<Student> call() {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] started filtering students by domain: {1}",
                    new Object[]{threadName, domain});
            try {
                List<Student> result = repository.findByEmailDomain(domain);
                logger.log(Level.INFO, "Thread [{0}] completed filtering. Found {1} students",
                        new Object[]{threadName, result.size()});
                return result;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Thread [{0}] error filtering students: {1}",
                        new Object[]{threadName, e.getMessage()});
                throw e;
            }
        }
    }

    public static class CountCoursesByCreditsTask implements Callable<Long> {
        private final CourseRepository repository;
        private final int minCredits;

        public CountCoursesByCreditsTask(CourseRepository repository, int minCredits) {
            this.repository = repository;
            this.minCredits = minCredits;
        }

        @Override
        public Long call() {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] started counting courses with credits >= {1}",
                    new Object[]{threadName, minCredits});
            try {
                long count = repository.getAll().stream()
                        .filter(c -> c.credits() >= minCredits)
                        .count();
                logger.log(Level.INFO, "Thread [{0}] completed counting. Found {1} courses",
                        new Object[]{threadName, count});
                return count;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Thread [{0}] error counting courses: {1}",
                        new Object[]{threadName, e.getMessage()});
                throw e;
            }
        }
    }

    public static class FindInstructorsByExpertiseTask implements Callable<List<Instructor>> {
        private final InstructorRepository repository;
        private final int minExpertise;

        public FindInstructorsByExpertiseTask(InstructorRepository repository, int minExpertise) {
            this.repository = repository;
            this.minExpertise = minExpertise;
        }

        @Override
        public List<Instructor> call() {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] started finding instructors with expertise >= {1}",
                    new Object[]{threadName, minExpertise});
            try {
                List<Instructor> result = repository.findByMinExpertise(minExpertise);
                logger.log(Level.INFO, "Thread [{0}] completed finding. Found {1} instructors",
                        new Object[]{threadName, result.size()});
                return result;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Thread [{0}] error finding instructors: {1}",
                        new Object[]{threadName, e.getMessage()});
                throw e;
            }
        }
    }

    public static class CalculateTotalCreditsTask implements Callable<Integer> {
        private final CourseRepository repository;

        public CalculateTotalCreditsTask(CourseRepository repository) {
            this.repository = repository;
        }

        @Override
        public Integer call() {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] started calculating total credits", threadName);
            try {
                int total = repository.getTotalCredits();
                logger.log(Level.INFO, "Thread [{0}] completed calculating. Total credits: {1}",
                        new Object[]{threadName, total});
                return total;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Thread [{0}] error calculating total credits: {1}",
                        new Object[]{threadName, e.getMessage()});
                throw e;
            }
        }
    }

    public static class PrintStudentsRunnable implements Runnable {
        private final StudentRepository repository;

        public PrintStudentsRunnable(StudentRepository repository) {
            this.repository = repository;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] started printing students", threadName);
            try {
                List<Student> students = repository.getAll();
                logger.log(Level.INFO, "Thread [{0}] processing {1} students",
                        new Object[]{threadName, students.size()});
                for (Student student : students) {
                    System.out.println("  " + student.firstName() + " " + student.lastName() + " - " + student.email());
                }
                logger.log(Level.INFO, "Thread [{0}] completed printing students", threadName);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Thread [{0}] error printing students: {1}",
                        new Object[]{threadName, e.getMessage()});
            }
        }
    }

    public static class CalculateAverageExpertiseTask implements Callable<Double> {
        private final InstructorRepository repository;

        public CalculateAverageExpertiseTask(InstructorRepository repository) {
            this.repository = repository;
        }

        @Override
        public Double call() {
            String threadName = Thread.currentThread().getName();
            logger.log(Level.INFO, "Thread [{0}] started calculating average expertise", threadName);
            try {
                double average = repository.getAverageExpertise();
                logger.log(Level.INFO, "Thread [{0}] completed calculating. Average expertise: {1}",
                        new Object[]{threadName, average});
                return average;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Thread [{0}] error calculating average expertise: {1}",
                        new Object[]{threadName, e.getMessage()});
                throw e;
            }
        }
    }
}
