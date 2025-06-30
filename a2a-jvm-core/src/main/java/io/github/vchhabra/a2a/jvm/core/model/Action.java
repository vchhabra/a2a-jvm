package io.github.vchhabra.a2a.jvm.core.model;

import java.util.Map;

/**
 * Describes a single action (tool) that an agent can execute.
 *
 * @param name The unique name of the action (e.g., "getWeatherForCity").
 * @param description A human-readable description of what the action does.
 * @param inputSchema A JSON Schema object describing the required input parameters.
 * @param operation A map defining how to invoke this action (e.g., via JSON-RPC).
 */
public record Action(
        String name,
        String description,
        Map<String, Object> inputSchema,
        Map<String, Object> operation) {}
