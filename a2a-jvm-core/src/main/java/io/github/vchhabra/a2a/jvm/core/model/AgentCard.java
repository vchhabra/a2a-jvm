package io.github.vchhabra.a2a.jvm.core.model;

import java.util.List;

import io.github.vchhabra.a2a.jvm.core.model.auth.AuthScheme;

/**
 * Represents the /.well-known/agent.json manifest file. It describes an agent's capabilities and
 * how to interact with it.
 *
 * @param id The unique identifier for the agent.
 * @param version The version of the agent specification.
 * @param actions A list of actions (tools) this agent can perform.
 * @param auth The authentication method required to interact with this agent.
 * @param api The API definition for how to interact with the agent's tasks.
 */
public record AgentCard(
        String id, String version, List<Action> actions, AuthScheme auth, Api api) {}
