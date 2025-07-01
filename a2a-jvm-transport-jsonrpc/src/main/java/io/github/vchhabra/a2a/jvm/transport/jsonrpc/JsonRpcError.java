package io.github.vchhabra.a2a.jvm.transport.jsonrpc;

/**
 * Represents a standard JSON-RPC 2.0 Error object, providing structured error information.
 *
 * @param code A Number that indicates the error type that occurred
 *             (e.g., -32601 for Method not found).
 * @param message A String providing a short, human-readable description of the error.
 * @param data A primitive or structured value containing additional information about the error.
 *             This member may be omitted.
 */
public record JsonRpcError(
        int code,
        String message,
        Object data
) {}
