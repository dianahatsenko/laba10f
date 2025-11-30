package ua.onlinecourses.repository;


@FunctionalInterface
public interface IdentityExtractor<T> {
    String extractIdentity(T object);
}