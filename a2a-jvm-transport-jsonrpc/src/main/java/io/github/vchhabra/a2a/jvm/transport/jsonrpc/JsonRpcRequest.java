package io.github.vchhabra.a2a.jvm.transport.jsonrpc;

import java.util.Map;

/**
 * Represents a standard JSON-RPC 2.0 request object, used for all A2A task communication.
 * This record is immutable and designed for serialization to and from JSON.
 *
 * @param jsonrpc The version of the JSON-RPC protocol, which MUST be "2.0".
 * @param method The name of the method/action to be invoked on the remote agent.
 * @param params The structured parameter values for the method, represented as a map.
 * @param id An identifier established by the Client, used to correlate requests and responses.
 *           It can be a String, a Number, or null.
 */
public record JsonRpcRequest(
        String jsonrpc,
        String method,
        Map<String, Object> params,
        Object id
) {}
