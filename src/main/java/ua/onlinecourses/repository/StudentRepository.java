package ua.onlinecourses.repository;

import ua.onlinecourses.model.Student;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class StudentRepository extends GenericRepository<Student> {
    private static final Logger logger = Logger.getLogger(StudentRepository.class.getName());

    public StudentRepository() {
        super(Student::email, "Student");
    }

    public List<Student> sortByName() {
        List<Student> allStudents = getAll();
        allStudents.sort(Student.BY_NAME);
        logger.log(Level.INFO, "Sorted Student by lastName, firstName, and email (ascending)");
        return allStudents;
    }

    public List<Student> sortByNameDesc() {
        List<Student> allStudents = getAll();
        allStudents.sort(Student.BY_NAME_DESC);
        logger.log(Level.INFO, "Sorted Student by lastName (desc), firstName, and email");
        return allStudents;
    }

    public List<Student> sortByEnrollmentDate() {
        List<Student> allStudents = getAll();
        allStudents.sort(Student.BY_ENROLLMENT_DATE);
        logger.log(Level.INFO, "Sorted Student by enrollment date");
        return allStudents;
    }

    public List<Student> sortByEnrollmentDateDesc() {
        List<Student> allStudents = getAll();
        allStudents.sort(Student.BY_ENROLLMENT_DATE.reversed());
        logger.log(Level.INFO, "Sorted Student by enrollment date (descending)");
        return allStudents;
    }

    public List<Student> sortByEmailLength() {
        List<Student> allStudents = getAll();
        allStudents.sort((s1, s2) -> Integer.compare(s1.email().length(), s2.email().length()));
        logger.log(Level.INFO, "Sorted Student by email length using lambda");
        return allStudents;
    }

    public List<Student> sortByFirstName() {
        List<Student> allStudents = getAll();
        allStudents.sort(Comparator.comparing(Student::firstName));
        logger.log(Level.INFO, "Sorted Student by firstName using method reference");
        return allStudents;
    }

    public List<Student> findByLastNameContaining(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            logger.log(Level.WARNING, "Attempted to search with null or empty partial name");
            return List.of();
        }

        String searchTerm = partialName.trim().toLowerCase();
        List<Student> results = getAll().stream()
                .filter(student -> student.lastName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} students with lastName containing ''{1}''",
                new Object[]{results.size(), partialName});
        return results;
    }

    public List<Student> findByFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            logger.log(Level.WARNING, "Attempted to search with null or empty firstName");
            return List.of();
        }

        List<Student> results = getAll().stream()
                .filter(student -> student.firstName().equalsIgnoreCase(firstName.trim()))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} students with firstName ''{1}''",
                new Object[]{results.size(), firstName});
        return results;
    }

    public List<Student> findByEnrollmentDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            logger.log(Level.WARNING, "Invalid date range: start={0}, end={1}",
                    new Object[]{startDate, endDate});
            return List.of();
        }

        List<Student> results = getAll().stream()
                .filter(student -> !student.enrollmentDate().isBefore(startDate) &&
                        !student.enrollmentDate().isAfter(endDate))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} students enrolled between {1} and {2}",
                new Object[]{results.size(), startDate, endDate});
        return results;
    }

    public List<Student> findByEmailDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            logger.log(Level.WARNING, "Attempted to search with null or empty domain");
            return List.of();
        }

        String searchDomain = domain.trim().toLowerCase();
        List<Student> results = getAll().stream()
                .filter(student -> student.email().toLowerCase().endsWith("@" + searchDomain))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} students with email domain ''{1}''",
                new Object[]{results.size(), domain});
        return results;
    }

    public Map<String, List<Student>> groupByLastName() {
        Map<String, List<Student>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(Student::lastName));

        logger.log(Level.INFO, "Grouped students by lastName: {0} groups", grouped.size());
        return grouped;
    }

    public Map<LocalDate, Long> countByEnrollmentDate() {
        Map<LocalDate, Long> counts = getAll().stream()
                .collect(Collectors.groupingBy(
                        Student::enrollmentDate,
                        Collectors.counting()
                ));

        logger.log(Level.INFO, "Student counts by enrollment date: {0}", counts);
        return counts;
    }

    public List<String> getAllEmails() {
        List<String> emails = getAll().stream()
                .map(Student::email)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Retrieved {0} student emails", emails.size());
        return emails;
    }

    public List<String> getAllFullNames() {
        List<String> fullNames = getAll().stream()
                .map(student -> student.firstName() + " " + student.lastName())
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Retrieved {0} student full names", fullNames.size());
        return fullNames;
    }

    public Optional<Student> findOldestStudent() {
        Optional<Student> oldest = getAll().stream()
                .min(Comparator.comparing(Student::enrollmentDate));

        if (oldest.isPresent()) {
            logger.log(Level.INFO, "Oldest student: {0} {1} (enrolled: {2})",
                    new Object[]{oldest.get().firstName(), oldest.get().lastName(),
                            oldest.get().enrollmentDate()});
        } else {
            logger.log(Level.INFO, "No students found");
        }

        return oldest;
    }

    public Optional<Student> findNewestStudent() {
        Optional<Student> newest = getAll().stream()
                .max(Comparator.comparing(Student::enrollmentDate));

        if (newest.isPresent()) {
            logger.log(Level.INFO, "Newest student: {0} {1} (enrolled: {2})",
                    new Object[]{newest.get().firstName(), newest.get().lastName(),
                            newest.get().enrollmentDate()});
        } else {
            logger.log(Level.INFO, "No students found");
        }

        return newest;
    }

    public long countByLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            logger.log(Level.WARNING, "Attempted to count with null or empty lastName");
            return 0;
        }

        long count = getAll().stream()
                .filter(student -> student.lastName().equalsIgnoreCase(lastName.trim()))
                .count();

        logger.log(Level.INFO, "Count of students with lastName ''{0}'': {1}",
                new Object[]{lastName, count});
        return count;
    }

    public boolean hasStudentWithEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.log(Level.WARNING, "Attempted to check with null or empty email");
            return false;
        }

        boolean exists = getAll().stream()
                .anyMatch(student -> student.email().equalsIgnoreCase(email.trim()));

        logger.log(Level.INFO, "Student with email ''{0}'' exists: {1}",
                new Object[]{email, exists});
        return exists;
    }

    public boolean allStudentsEnrolledAfter(LocalDate date) {
        if (date == null) {
            logger.log(Level.WARNING, "Attempted to check with null date");
            return false;
        }

        boolean result = getAll().stream()
                .allMatch(student -> student.enrollmentDate().isAfter(date));

        logger.log(Level.INFO, "All students enrolled after {0}: {1}",
                new Object[]{date, result});
        return result;
    }

    public void printAllStudents() {
        logger.log(Level.INFO, "Printing all students:");
        getAll().stream()
                .forEach(student -> System.out.println(student.firstName() + " " +
                        student.lastName() + " - " +
                        student.email()));
    }
}