package ua.onlinecourses.repository;


import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class GenericRepository<T> {
    private static final Logger logger = Logger.getLogger(GenericRepository.class.getName());


    private final List<T> items;
    private final IdentityExtractor<T> identityExtractor;
    private final String entityType;

    public GenericRepository(IdentityExtractor<T> identityExtractor, String entityType) {
        this.items = new CopyOnWriteArrayList<>();
        this.identityExtractor = identityExtractor;
        this.entityType = entityType;
        logger.log(Level.INFO,"Created repository for {0}", entityType);
    }

    public synchronized boolean add(T item) {
        if (item == null) {
            logger.log(Level.WARNING, "Attempted to add null {0}", entityType);
            return false;
        }

        String identity = identityExtractor.extractIdentity(item);
        if (findByIdentity(identity).isPresent()) {
            logger.log(Level.WARNING,"Cannot add {0} - already exists with identity: {1}",new Object[]{entityType, identity});
            return false;
        }

        boolean added = items.add(item);
        if (added) {
            logger.log(Level.INFO, "Added {0}: {1}", new Object[]{entityType, identity});
        }
        return added;
    }


    public boolean remove(T item) {
        if (item == null) {
            logger.log(Level.WARNING, "Attempted to remove null {0}", entityType);
            return false;
        }

        boolean removed = items.remove(item);
        if (removed) {
            logger.log(Level.INFO, "Removed {0}: {1}", new Object[]{entityType, identityExtractor.extractIdentity(item)});
        } else {
            logger.log(Level.WARNING,"Failed to remove {0}: {1}", new Object[]{entityType, identityExtractor.extractIdentity(item)});
        }
        return removed;
    }


    public boolean removeByIdentity(String identity) {
        if (identity == null) {
            logger.log(Level.WARNING,"Attempted to remove {0} with null identity", entityType);
            return false;
        }

        Optional<T> itemToRemove = items.stream()
                .filter(item -> identity.equals(identityExtractor.extractIdentity(item)))
                .findFirst();

        if (itemToRemove.isPresent()) {
            boolean removed = items.remove(itemToRemove.get());
            if (removed) {
                logger.log(Level.INFO, "Removed {0} by identity: {1}", new Object[]{entityType, identity});
            }
            return removed;
        } else {
            logger.log(Level.WARNING,"No {0} found with identity: {1} to remove", new Object[]{entityType, identity});
            return false;
        }
    }

    public boolean contains(T item) {
        return items.contains(item); // Uses equals() internally
    }

    public boolean containsIdentity(String identity) {
        return findByIdentity(identity).isPresent();
    }

    public Optional<T> findByIdentity(String identity) {
        if (identity == null) {
            logger.log(Level.WARNING,"Attempted to find {0} with null identity", entityType);
            return Optional.empty();
        }

        Optional<T> result = items.stream()
                .filter(item -> identity.equals(identityExtractor.extractIdentity(item)))
                .findFirst();

        if (result.isPresent()) {
            logger.log(Level.INFO,"Found {0} with identity: {1}", new Object[]{entityType, identity});
        } else {
            logger.log(Level.INFO,"No {0} found with identity: {1}", new Object[]{entityType, identity});
        }

        return result;
    }

    public List<T> getAll() {
        logger.log(Level.INFO,"Retrieved all {0} items. Count: {1}", new Object[]{entityType, items.size()} );
        return new ArrayList<>(items);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        int sizeBefore = items.size();
        items.clear();
        logger.log(Level.INFO,"Cleared repository. Removed {0} {1} items", new Object[]{sizeBefore, entityType});
    }

    public List<T> sortByIdentity(String order) {
        if (order == null || (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))) {
            logger.log(Level.WARNING, "Invalid sort order: {0}. Using 'asc' by default", order);
            order = "asc";
        }

        List<T> sortedItems = new ArrayList<>(items);

        Comparator<T> comparator = Comparator.comparing(identityExtractor::extractIdentity);

        if (order.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }

        sortedItems.sort(comparator);

        logger.log(Level.INFO, "Sorted {0} items by identity in {1} order",
                new Object[]{entityType, order});

        return sortedItems;
    }


    List<T> getItemsForTesting() {
        return items;
    }
}