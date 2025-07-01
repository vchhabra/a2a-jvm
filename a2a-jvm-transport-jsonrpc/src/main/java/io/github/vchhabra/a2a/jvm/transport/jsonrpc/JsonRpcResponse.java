package io.github.vchhabra.a2a.jvm.transport.jsonrpc;

/**
 * Represents a standard JSON-RPC 2.0 response object.
 * A valid response MUST contain either the 'result' or 'error' member, but not both.
 *
 * @param jsonrpc The version of the JSON-RPC protocol - MUST be "2.0".
 * @param result The value returned by the invoked method.
 *               This member is omitted if an error occurred.
 * @param error An error object if an error occurred during the invocation.
 *              This member is omitted if no error occurred.
 * @param id The identifier from the original Request object it is responding to.
 */
public record JsonRpcResponse(
    String jsonrpc,
    Object result,
    JsonRpcError error,
    Object id
) {}
