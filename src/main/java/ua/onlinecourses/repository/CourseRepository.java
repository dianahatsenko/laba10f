package ua.onlinecourses.repository;

import ua.onlinecourses.model.Course;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CourseRepository extends GenericRepository<Course> {
    private static final Logger logger = Logger.getLogger(CourseRepository.class.getName());

    public CourseRepository() {
        super(Course::getFullName, "Course");
    }

    public List<Course> sortByTitle() {
        List<Course> allCourses = getAll();
        allCourses.sort(Course.BY_TITLE);
        logger.log(Level.INFO, "Sorted Course by title");
        return allCourses;
    }

    public List<Course> sortByCredits() {
        List<Course> allCourses = getAll();
        allCourses.sort(Course.BY_CREDITS);
        logger.log(Level.INFO, "Sorted Course by credits");
        return allCourses;
    }

    public List<Course> sortByCreditsDesc() {
        List<Course> allCourses = getAll();
        allCourses.sort(Course.BY_CREDITS.reversed());
        logger.log(Level.INFO, "Sorted Course by credits (descending)");
        return allCourses;
    }

    public List<Course> sortByStartDate() {
        List<Course> allCourses = getAll();
        allCourses.sort(Course.BY_START_DATE);
        logger.log(Level.INFO, "Sorted Course by start date");
        return allCourses;
    }

    public List<Course> sortByDescription() {
        List<Course> allCourses = getAll();
        allCourses.sort((c1, c2) -> c1.description().compareTo(c2.description()));
        logger.log(Level.INFO, "Sorted Course by description using lambda");
        return allCourses;
    }

    public List<Course> sortByCreditsAndDate() {
        List<Course> allCourses = getAll();
        allCourses.sort(
                Comparator.comparingInt(Course::credits).reversed()
                        .thenComparing(Course::startDate)
        );
        logger.log(Level.INFO, "Sorted Course by credits (desc) and start date");
        return allCourses;
    }

    public List<Course> findByTitleContaining(String partialTitle) {
        if (partialTitle == null || partialTitle.trim().isEmpty()) {
            logger.log(Level.WARNING, "Attempted to search with null or empty partial title");
            return List.of();
        }

        String searchTerm = partialTitle.trim().toLowerCase();
        List<Course> results = getAll().stream()
                .filter(course -> course.title().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} courses with title containing ''{1}''",
                new Object[]{results.size(), partialTitle});
        return results;
    }

    public List<Course> findByCreditsRange(int minCredits, int maxCredits) {
        if (minCredits > maxCredits) {
            logger.log(Level.WARNING, "Invalid credit range: min={0} > max={1}",
                    new Object[]{minCredits, maxCredits});
            return List.of();
        }

        List<Course> results = getAll().stream()
                .filter(course -> course.credits() >= minCredits && course.credits() <= maxCredits)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} courses with credits between {1} and {2}",
                new Object[]{results.size(), minCredits, maxCredits});
        return results;
    }

    public List<Course> findByStartDateAfter(LocalDate date) {
        if (date == null) {
            logger.log(Level.WARNING, "Attempted to search with null date");
            return List.of();
        }

        List<Course> results = getAll().stream()
                .filter(course -> course.startDate().isAfter(date))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} courses starting after {1}",
                new Object[]{results.size(), date});
        return results;
    }

    public List<Course> findByDescriptionContaining(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            logger.log(Level.WARNING, "Attempted to search with null or empty keyword");
            return List.of();
        }

        String searchTerm = keyword.trim().toLowerCase();
        List<Course> results = getAll().stream()
                .filter(course -> course.description().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} courses with description containing ''{1}''",
                new Object[]{results.size(), keyword});
        return results;
    }

    public Map<Integer, List<Course>> groupByCredits() {
        Map<Integer, List<Course>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(Course::credits));

        logger.log(Level.INFO, "Grouped courses by credits: {0} groups", grouped.size());
        return grouped;
    }

    public int getTotalCredits() {
        int total = getAll().stream()
                .map(Course::credits)
                .reduce(0, Integer::sum);

        logger.log(Level.INFO, "Total credits across all courses: {0}", total);
        return total;
    }

    public double getAverageCredits() {
        double average = getAll().stream()
                .mapToInt(Course::credits)
                .average()
                .orElse(0.0);

        logger.log(Level.INFO, "Average credits: {0}", average);
        return average;
    }

    public Optional<Course> getCourseWithMaxCredits() {
        Optional<Course> result = getAll().stream()
                .max(Comparator.comparingInt(Course::credits));

        if (result.isPresent()) {
            logger.log(Level.INFO, "Course with max credits: {0} ({1} credits)",
                    new Object[]{result.get().title(), result.get().credits()});
        } else {
            logger.log(Level.INFO, "No courses found");
        }

        return result;
    }

    public List<Course> getAllCoursesWithMaxCredits() {
        List<Course> allCourses = getAll();
        if (allCourses.isEmpty()) {
            logger.log(Level.INFO, "No courses found");
            return List.of();
        }

        int maxCredits = allCourses.stream()
                .mapToInt(Course::credits)
                .max()
                .orElse(0);

        List<Course> results = allCourses.stream()
                .filter(course -> course.credits() == maxCredits)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} course(s) with max credits: {1} credits",
                new Object[]{results.size(), maxCredits});
        return results;
    }

    public List<String> getAllTitles() {
        List<String> titles = getAll().stream()
                .map(Course::title)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Retrieved {0} course titles", titles.size());
        return titles;
    }

    public boolean hasCourseWithCredits(int credits) {
        boolean exists = getAll().stream()
                .anyMatch(course -> course.credits() == credits);

        logger.log(Level.INFO, "Courses with {0} credits exist: {1}",
                new Object[]{credits, exists});
        return exists;
    }

    public boolean allCoursesHaveMinCredits(int minCredits) {
        boolean result = getAll().stream()
                .allMatch(course -> course.credits() >= minCredits);

        logger.log(Level.INFO, "All courses have >= {0} credits: {1}",
                new Object[]{minCredits, result});
        return result;
    }

    public void printAllCourses() {
        logger.log(Level.INFO, "Printing all courses:");
        getAll().stream()
                .forEach(course -> System.out.println(course.title() + " - " +
                        course.credits() + " credits"));
    }
}