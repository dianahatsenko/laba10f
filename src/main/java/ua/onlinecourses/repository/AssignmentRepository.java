package ua.onlinecourses.repository;

import ua.onlinecourses.model.Assignment;
import ua.onlinecourses.model.Mark;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AssignmentRepository extends GenericRepository<Assignment> {
    private static final Logger logger = Logger.getLogger(AssignmentRepository.class.getName());

    public AssignmentRepository() {
        super(Assignment::getIdentity, "Assignment");
    }

    public List<Assignment> sortByDueDate() {
        List<Assignment> allAssignments = getAll();
        allAssignments.sort(Assignment.BY_DUE_DATE);
        logger.log(Level.INFO, "Sorted Assignment by due date");
        return allAssignments;
    }

    public List<Assignment> sortByDueDateDesc() {
        List<Assignment> allAssignments = getAll();
        allAssignments.sort(Assignment.BY_DUE_DATE.reversed());
        logger.log(Level.INFO, "Sorted Assignment by due date (descending)");
        return allAssignments;
    }

    public List<Assignment> sortByMaxPoints() {
        List<Assignment> allAssignments = getAll();
        allAssignments.sort(Assignment.BY_MAX_POINTS);
        logger.log(Level.INFO, "Sorted Assignment by max points (descending)");
        return allAssignments;
    }

    public List<Assignment> sortByMark() {
        List<Assignment> allAssignments = getAll();
        allAssignments.sort(Assignment.BY_MARK);
        logger.log(Level.INFO, "Sorted Assignment by mark");
        return allAssignments;
    }

    public List<Assignment> sortByModuleAndDate() {
        List<Assignment> allAssignments = getAll();
        allAssignments.sort(Assignment.BY_MODULE_AND_DATE);
        logger.log(Level.INFO, "Sorted Assignment by module and due date");
        return allAssignments;
    }

    public List<Assignment> sortByModuleTitle() {
        List<Assignment> allAssignments = getAll();
        allAssignments.sort((a1, a2) -> a1.module().title().compareTo(a2.module().title()));
        logger.log(Level.INFO, "Sorted Assignment by module title using lambda");
        return allAssignments;
    }

    public List<Assignment> sortByMarkPointsDate() {
        List<Assignment> allAssignments = getAll();
        allAssignments.sort(
                Comparator.comparing(Assignment::mark)
                        .thenComparingInt(Assignment::maxPoints).reversed()
                        .thenComparing(Assignment::dueDate)
        );
        logger.log(Level.INFO, "Sorted Assignment by mark, max points (desc), and due date");
        return allAssignments;
    }

    public List<Assignment> sortNaturally() {
        List<Assignment> allAssignments = getAll();
        allAssignments.sort(null);
        logger.log(Level.INFO, "Sorted Assignment using natural order (Comparable)");
        return allAssignments;
    }

    public List<Assignment> findByMark(Mark mark) {
        if (mark == null) {
            logger.log(Level.WARNING, "Attempted to search with null mark");
            return List.of();
        }

        List<Assignment> results = getAll().stream()
                .filter(assignment -> assignment.mark() == mark)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} assignments with mark ''{1}''",
                new Object[]{results.size(), mark});
        return results;
    }

    public List<Assignment> findByPointsRange(int minPoints, int maxPoints) {
        if (minPoints > maxPoints) {
            logger.log(Level.WARNING, "Invalid points range: min={0} > max={1}",
                    new Object[]{minPoints, maxPoints});
            return List.of();
        }

        List<Assignment> results = getAll().stream()
                .filter(assignment -> assignment.maxPoints() >= minPoints &&
                        assignment.maxPoints() <= maxPoints)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} assignments with points between {1} and {2}",
                new Object[]{results.size(), minPoints, maxPoints});
        return results;
    }

    public List<Assignment> findByDueDateBefore(LocalDate date) {
        if (date == null) {
            logger.log(Level.WARNING, "Attempted to search with null date");
            return List.of();
        }

        List<Assignment> results = getAll().stream()
                .filter(assignment -> assignment.dueDate().isBefore(date))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} assignments due before {1}",
                new Object[]{results.size(), date});
        return results;
    }

    public List<Assignment> findByDueDateAfter(LocalDate date) {
        if (date == null) {
            logger.log(Level.WARNING, "Attempted to search with null date");
            return List.of();
        }

        List<Assignment> results = getAll().stream()
                .filter(assignment -> assignment.dueDate().isAfter(date))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} assignments due after {1}",
                new Object[]{results.size(), date});
        return results;
    }

    public List<Assignment> findByModuleTitle(String moduleTitle) {
        if (moduleTitle == null || moduleTitle.trim().isEmpty()) {
            logger.log(Level.WARNING, "Attempted to search with null or empty module title");
            return List.of();
        }

        String searchTerm = moduleTitle.trim().toLowerCase();
        List<Assignment> results = getAll().stream()
                .filter(assignment -> assignment.module().title().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} assignments with module title containing ''{1}''",
                new Object[]{results.size(), moduleTitle});
        return results;
    }

    public Map<Mark, List<Assignment>> groupByMark() {
        Map<Mark, List<Assignment>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(Assignment::mark));

        logger.log(Level.INFO, "Grouped assignments by mark: {0} groups", grouped.size());
        return grouped;
    }

    public Map<String, List<Assignment>> groupByModuleTitle() {
        Map<String, List<Assignment>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(assignment -> assignment.module().title()));

        logger.log(Level.INFO, "Grouped assignments by module title: {0} groups", grouped.size());
        return grouped;
    }

    public int getTotalMaxPoints() {
        int total = getAll().stream()
                .map(Assignment::maxPoints)
                .reduce(0, Integer::sum);

        logger.log(Level.INFO, "Total max points across all assignments: {0}", total);
        return total;
    }

    public double getAverageMaxPoints() {
        double average = getAll().stream()
                .mapToInt(Assignment::maxPoints)
                .average()
                .orElse(0.0);

        logger.log(Level.INFO, "Average max points: {0}", average);
        return average;
    }

    public Optional<Assignment> getAssignmentWithMaxPoints() {
        Optional<Assignment> result = getAll().stream()
                .max(Comparator.comparingInt(Assignment::maxPoints));

        if (result.isPresent()) {
            logger.log(Level.INFO, "Assignment with max points: {0} ({1} points)",
                    new Object[]{result.get().module().title(), result.get().maxPoints()});
        } else {
            logger.log(Level.INFO, "No assignments found");
        }

        return result;
    }

    public List<Assignment> getAllAssignmentsWithMaxPoints() {
        List<Assignment> allAssignments = getAll();
        if (allAssignments.isEmpty()) {
            logger.log(Level.INFO, "No assignments found");
            return List.of();
        }

        int maxPoints = allAssignments.stream()
                .mapToInt(Assignment::maxPoints)
                .max()
                .orElse(0);

        List<Assignment> results = allAssignments.stream()
                .filter(assignment -> assignment.maxPoints() == maxPoints)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} assignment(s) with max points: {1} points",
                new Object[]{results.size(), maxPoints});
        return results;
    }

    public List<String> getAllModuleTitles() {
        List<String> titles = getAll().stream()
                .map(assignment -> assignment.module().title())
                .distinct()
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Retrieved {0} unique module titles", titles.size());
        return titles;
    }

    public boolean hasAssignmentWithPoints(int points) {
        boolean exists = getAll().stream()
                .anyMatch(assignment -> assignment.maxPoints() == points);

        logger.log(Level.INFO, "Assignments with {0} points exist: {1}",
                new Object[]{points, exists});
        return exists;
    }

    public boolean allAssignmentsHaveMinPoints(int minPoints) {
        boolean result = getAll().stream()
                .allMatch(assignment -> assignment.maxPoints() >= minPoints);

        logger.log(Level.INFO, "All assignments have >= {0} points: {1}",
                new Object[]{minPoints, result});
        return result;
    }

    public void printAllAssignments() {
        logger.log(Level.INFO, "Printing all assignments:");
        getAll().stream()
                .forEach(assignment -> System.out.println(assignment.module().title() +
                        " - due: " + assignment.dueDate() +
                        ", points: " + assignment.maxPoints()));
    }
}