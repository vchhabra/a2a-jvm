package io.github.vchhabra.a2a.jvm.core.service;

import java.util.Optional;

import io.github.vchhabra.a2a.jvm.core.model.Task;

/**
 * Defines the contract for a component responsible for persisting the state of asynchronous tasks.
 * This SPI (Service Provider Interface) allows developers to provide their own storage implementations
 * (e.g., in-memory, Redis, JDBC) while the core SDK logic remains unchanged.
 */
public interface TaskStore {

    /**
     * Saves or updates a task's state in the persistence layer.
     *
     * @param task The task object to save.
     */
    void save(Task task);

    /**
     * Finds and retrieves a task by its unique identifier.
     *
     * @param taskId The unique ID of the task to find.
     * @return An Optional containing the Task if found, otherwise an empty Optional.
     */
    Optional<Task> findById(String taskId);
}
