package io.github.vchhabra.a2a.jvm.core.model.enums;

/**
 * Enum representing the various states of a long-running, asynchronous task. These states define
 * the lifecycle of a task from submission to completion or failure.
 *
 * <p>Constants: - SUBMITTED: Indicates that the task has been submitted but not yet started. -
 * WORKING: Indicates that the task is currently in progress. - COMPLETED: Indicates that the task
 * has successfully completed. - FAILED: Indicates that the task has encountered an error and
 * failed. - CANCELLED: Indicates that the task has been cancelled before completion.
 *
 * <p>Each constant is associated with a string value representing its state.
 */
public enum TaskState {
    SUBMITTED("submitted"),
    WORKING("working"),
    COMPLETED("completed"),
    FAILED("failed"),
    CANCELLED("cancelled");

    private final String value;

    TaskState(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
