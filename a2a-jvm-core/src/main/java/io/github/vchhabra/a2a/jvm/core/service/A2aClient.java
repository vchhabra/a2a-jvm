package io.github.vchhabra.a2a.jvm.core.service;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.github.vchhabra.a2a.jvm.core.model.AgentCard;
import io.github.vchhabra.a2a.jvm.core.model.Task;

/**
 * Defines the public contract for a client capable of communicating with an A2A-compliant agent.
 * This interface provides the primary methods for agent discovery and asynchronous task management.
 */
public interface A2aClient {

    /**
     * Discovers an agent by fetching and parsing its AgentCard from the well-known URI.
     *
     * @param agentUri The base URI of the target agent.
     * @return A CompletableFuture which will complete with the discovered AgentCard,
     *     or an exception if discovery fails.
     */
    CompletableFuture<AgentCard> discoverAgent(URI agentUri);

    /**
     * Creates a new asynchronous task on a target agent.
     *
     * @param targetAgent The AgentCard of the target agent, containing the necessary API endpoint information.
     * @param actionName The name of the action to invoke, which must be listed in the targetAgent's actions.
     * @param params A map of parameters for the action, conforming to the action's input schema.
     * @return A CompletableFuture which will complete with the initial Task object returned by the server,
     *     indicating the task has been submitted.
     */
    CompletableFuture<Task> createTask(AgentCard targetAgent, String actionName, Map<String, Object> params);

    /**
     * Fetches the current status and result of a previously created task.
     *
     * @param task The task object whose status is to be fetched. The taskId and the target agent's API URL are used.
     * @return A CompletableFuture which will complete with the updated Task object, potentially containing the result
     *      or an error.
     */
    CompletableFuture<Task> getTaskStatus(Task task);
}
