package ua.onlinecourses.repository;

import ua.onlinecourses.model.Instructor;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class InstructorRepository extends GenericRepository<Instructor> {
    private static final Logger logger = Logger.getLogger(InstructorRepository.class.getName());

    public InstructorRepository() {
        super(Instructor::getFullName, "Instructor");
    }

    public List<Instructor> sortByExpertise() {
        List<Instructor> allInstructors = getAll();
        allInstructors.sort(Instructor.BY_EXPERTISE);
        logger.log(Level.INFO, "Sorted Instructor by expertise level (descending)");
        return allInstructors;
    }

    public List<Instructor> sortByLastName() {
        List<Instructor> allInstructors = getAll();
        allInstructors.sort(Instructor.BY_LAST_NAME);
        logger.log(Level.INFO, "Sorted Instructor by lastName and firstName");
        return allInstructors;
    }

    public List<Instructor> sortByFirstName() {
        List<Instructor> allInstructors = getAll();
        allInstructors.sort(Instructor.BY_FIRST_NAME);
        logger.log(Level.INFO, "Sorted Instructor by firstName and lastName");
        return allInstructors;
    }

    public List<Instructor> sortByLastNameLength() {
        List<Instructor> allInstructors = getAll();
        allInstructors.sort((i1, i2) -> {
            int lengthCompare = Integer.compare(i1.lastName().length(), i2.lastName().length());
            if (lengthCompare != 0) {
                return lengthCompare;
            }
            return i1.lastName().compareTo(i2.lastName());
        });
        logger.log(Level.INFO, "Sorted Instructor by lastName length using lambda");
        return allInstructors;
    }

    public List<Instructor> sortByExpertiseAndName() {
        List<Instructor> allInstructors = getAll();
        allInstructors.sort(
                Comparator.comparingInt(Instructor::expertise).reversed()
                        .thenComparing(Instructor::lastName)
                        .thenComparing(Instructor::firstName)
        );
        logger.log(Level.INFO, "Sorted Instructor by expertise (desc) and name");
        return allInstructors;
    }

    public List<Instructor> findByLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            logger.log(Level.WARNING, "Attempted to search with null or empty lastName");
            return List.of();
        }

        List<Instructor> results = getAll().stream()
                .filter(instructor -> instructor.lastName().equalsIgnoreCase(lastName.trim()))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} instructors with lastName ''{1}''",
                new Object[]{results.size(), lastName});
        return results;
    }

    public List<Instructor> findByExpertiseRange(int minExpertise, int maxExpertise) {
        if (minExpertise > maxExpertise) {
            logger.log(Level.WARNING, "Invalid expertise range: min={0} > max={1}",
                    new Object[]{minExpertise, maxExpertise});
            return List.of();
        }

        List<Instructor> results = getAll().stream()
                .filter(instructor -> instructor.expertise() >= minExpertise &&
                        instructor.expertise() <= maxExpertise)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} instructors with expertise between {1} and {2}",
                new Object[]{results.size(), minExpertise, maxExpertise});
        return results;
    }

    public List<Instructor> findByMinExpertise(int minExpertise) {
        List<Instructor> results = getAll().stream()
                .filter(instructor -> instructor.expertise() >= minExpertise)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} instructors with expertise >= {1}",
                new Object[]{results.size(), minExpertise});
        return results;
    }

    public List<Instructor> findByFirstNameContaining(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            logger.log(Level.WARNING, "Attempted to search with null or empty partial name");
            return List.of();
        }

        String searchTerm = partialName.trim().toLowerCase();
        List<Instructor> results = getAll().stream()
                .filter(instructor -> instructor.firstName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} instructors with firstName containing ''{1}''",
                new Object[]{results.size(), partialName});
        return results;
    }

    public Map<String, List<Instructor>> groupByLastName() {
        Map<String, List<Instructor>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(Instructor::lastName));

        logger.log(Level.INFO, "Grouped instructors by lastName: {0} groups", grouped.size());
        return grouped;
    }

    public int getTotalExpertise() {
        int total = getAll().stream()
                .map(Instructor::expertise)
                .reduce(0, Integer::sum);

        logger.log(Level.INFO, "Total expertise across all instructors: {0}", total);
        return total;
    }

    public double getAverageExpertise() {
        double average = getAll().stream()
                .mapToInt(Instructor::expertise)
                .average()
                .orElse(0.0);

        logger.log(Level.INFO, "Average expertise: {0}", average);
        return average;
    }

    public Optional<Instructor> getInstructorWithMaxExpertise() {
        Optional<Instructor> result = getAll().stream()
                .max(Comparator.comparingInt(Instructor::expertise));

        if (result.isPresent()) {
            logger.log(Level.INFO, "Instructor with max expertise: {0} {1} ({2})",
                    new Object[]{result.get().firstName(), result.get().lastName(),
                            result.get().expertise()});
        } else {
            logger.log(Level.INFO, "No instructors found");
        }

        return result;
    }

    public List<Instructor> getAllInstructorsWithMaxExpertise() {
        List<Instructor> allInstructors = getAll();
        if (allInstructors.isEmpty()) {
            logger.log(Level.INFO, "No instructors found");
            return List.of();
        }

        int maxExpertise = allInstructors.stream()
                .mapToInt(Instructor::expertise)
                .max()
                .orElse(0);

        List<Instructor> results = allInstructors.stream()
                .filter(instructor -> instructor.expertise() == maxExpertise)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} instructor(s) with max expertise: {1}",
                new Object[]{results.size(), maxExpertise});
        return results;
    }

    public List<String> getAllFullNames() {
        List<String> fullNames = getAll().stream()
                .map(instructor -> instructor.firstName() + " " + instructor.lastName())
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Retrieved {0} instructor full names", fullNames.size());
        return fullNames;
    }

    public boolean hasInstructorWithExpertise(int expertise) {
        boolean exists = getAll().stream()
                .anyMatch(instructor -> instructor.expertise() == expertise);

        logger.log(Level.INFO, "Instructors with expertise {0} exist: {1}",
                new Object[]{expertise, exists});
        return exists;
    }

    public boolean allInstructorsHaveMinExpertise(int minExpertise) {
        boolean result = getAll().stream()
                .allMatch(instructor -> instructor.expertise() >= minExpertise);

        logger.log(Level.INFO, "All instructors have >= {0} expertise: {1}",
                new Object[]{minExpertise, result});
        return result;
    }

    public void printAllInstructors() {
        logger.log(Level.INFO, "Printing all instructors:");
        getAll().stream()
                .forEach(instructor -> System.out.println(instructor.firstName() + " " +
                        instructor.lastName() + " - expertise: " +
                        instructor.expertise()));
    }
}