package ua.onlinecourses.repository;

import ua.onlinecourses.model.myModule;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ModuleRepository extends GenericRepository<myModule> {
    private static final Logger logger = Logger.getLogger(ModuleRepository.class.getName());

    public ModuleRepository() {
        super(myModule::getFullName, "Module");
    }

    public List<myModule> sortByTitle() {
        List<myModule> allModules = getAll();
        allModules.sort(myModule.BY_TITLE);
        logger.log(Level.INFO, "Sorted Module by title");
        return allModules;
    }

    public List<myModule> sortByContent() {
        List<myModule> allModules = getAll();
        allModules.sort(myModule.BY_CONTENT);
        logger.log(Level.INFO, "Sorted Module by content");
        return allModules;
    }

    public List<myModule> sortByContentLength() {
        List<myModule> allModules = getAll();
        allModules.sort(myModule.BY_CONTENT_LENGTH);
        logger.log(Level.INFO, "Sorted Module by content length");
        return allModules;
    }

    public List<myModule> sortByTitleLength() {
        List<myModule> allModules = getAll();
        allModules.sort((m1, m2) -> {
            int lengthCompare = Integer.compare(m1.title().length(), m2.title().length());
            return lengthCompare != 0 ? lengthCompare : m1.title().compareTo(m2.title());
        });
        logger.log(Level.INFO, "Sorted Module by title length using lambda");
        return allModules;
    }

    public List<myModule> sortByTotalLength() {
        List<myModule> allModules = getAll();
        allModules.sort(
                Comparator.comparingInt((myModule m) -> m.title().length() + m.content().length())
                        .thenComparing(myModule::title)
        );
        logger.log(Level.INFO, "Sorted Module by total length (title + content)");
        return allModules;
    }

    public List<myModule> sortNaturally() {
        List<myModule> allModules = getAll();
        allModules.sort(null);
        logger.log(Level.INFO, "Sorted Module using natural order (Comparable)");
        return allModules;
    }

    public List<myModule> findByTitleContaining(String partialTitle) {
        if (partialTitle == null || partialTitle.trim().isEmpty()) {
            logger.log(Level.WARNING, "Attempted to search with null or empty partial title");
            return List.of();
        }

        String searchTerm = partialTitle.trim().toLowerCase();
        List<myModule> results = getAll().stream()
                .filter(module -> module.title().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} modules with title containing ''{1}''",
                new Object[]{results.size(), partialTitle});
        return results;
    }

    public List<myModule> findByContentContaining(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            logger.log(Level.WARNING, "Attempted to search with null or empty keyword");
            return List.of();
        }

        String searchTerm = keyword.trim().toLowerCase();
        List<myModule> results = getAll().stream()
                .filter(module -> module.content().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} modules with content containing ''{1}''",
                new Object[]{results.size(), keyword});
        return results;
    }

    public List<myModule> findByTitleLengthRange(int minLength, int maxLength) {
        if (minLength > maxLength) {
            logger.log(Level.WARNING, "Invalid length range: min={0} > max={1}",
                    new Object[]{minLength, maxLength});
            return List.of();
        }

        List<myModule> results = getAll().stream()
                .filter(module -> module.title().length() >= minLength &&
                        module.title().length() <= maxLength)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} modules with title length between {1} and {2}",
                new Object[]{results.size(), minLength, maxLength});
        return results;
    }

    public List<myModule> findByContentLengthRange(int minLength, int maxLength) {
        if (minLength > maxLength) {
            logger.log(Level.WARNING, "Invalid length range: min={0} > max={1}",
                    new Object[]{minLength, maxLength});
            return List.of();
        }

        List<myModule> results = getAll().stream()
                .filter(module -> module.content().length() >= minLength &&
                        module.content().length() <= maxLength)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Found {0} modules with content length between {1} and {2}",
                new Object[]{results.size(), minLength, maxLength});
        return results;
    }

    public Map<Integer, List<myModule>> groupByTitleLength() {
        Map<Integer, List<myModule>> grouped = getAll().stream()
                .collect(Collectors.groupingBy(module -> module.title().length()));

        logger.log(Level.INFO, "Grouped modules by title length: {0} groups", grouped.size());
        return grouped;
    }

    public int getTotalContentLength() {
        int total = getAll().stream()
                .map(module -> module.content().length())
                .reduce(0, Integer::sum);

        logger.log(Level.INFO, "Total content length across all modules: {0}", total);
        return total;
    }

    public double getAverageContentLength() {
        double average = getAll().stream()
                .mapToInt(module -> module.content().length())
                .average()
                .orElse(0.0);

        logger.log(Level.INFO, "Average content length: {0}", average);
        return average;
    }

    public Optional<myModule> getModuleWithLongestContent() {
        Optional<myModule> result = getAll().stream()
                .max(Comparator.comparingInt(module -> module.content().length()));

        if (result.isPresent()) {
            logger.log(Level.INFO, "Module with longest content: {0} ({1} chars)",
                    new Object[]{result.get().title(), result.get().content().length()});
        } else {
            logger.log(Level.INFO, "No modules found");
        }

        return result;
    }

    public List<String> getAllTitles() {
        List<String> titles = getAll().stream()
                .map(myModule::title)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Retrieved {0} module titles", titles.size());
        return titles;
    }

    public List<String> getAllTitlesUpperCase() {
        List<String> titles = getAll().stream()
                .map(myModule::title)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Retrieved {0} module titles in uppercase", titles.size());
        return titles;
    }

    public boolean hasModuleWithTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            logger.log(Level.WARNING, "Attempted to check with null or empty title");
            return false;
        }

        boolean exists = getAll().stream()
                .anyMatch(module -> module.title().equalsIgnoreCase(title.trim()));

        logger.log(Level.INFO, "Module with title ''{0}'' exists: {1}",
                new Object[]{title, exists});
        return exists;
    }

    public boolean allModulesHaveMinContentLength(int minLength) {
        boolean result = getAll().stream()
                .allMatch(module -> module.content().length() >= minLength);

        logger.log(Level.INFO, "All modules have content length >= {0}: {1}",
                new Object[]{minLength, result});
        return result;
    }

    public void printAllModules() {
        logger.log(Level.INFO, "Printing all modules:");
        getAll().stream()
                .forEach(module -> System.out.println(module.title() + " - " +
                        module.content()));
    }
}