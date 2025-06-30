package io.github.vchhabra.a2a.jvm.core.model;

import io.github.vchhabra.a2a.jvm.core.model.enums.TaskState;

/**
 * Represents the state and result of a long-running, asynchronous task.
 *
 * @param taskId A unique identifier for the task.
 * @param state  The current state of the task.
 * @param result The result of the task. Only present if the state is "completed".
 * @param error  An error object. Only present if the state is "failed".
 */
public record Task(
        String taskId,
        TaskState state,
        Object result,
        Object error // Using Object to match JSON-RPC error structure
) {}
