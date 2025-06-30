package io.github.vchhabra.a2a.jvm.core.model.enums;

/**
 * Enum representing supported API types for task management. Provides a predefined value indicating
 * the type of API endpoint, such as `json_rpc_http`. This information is used in the context of
 * creating or managing tasks.
 */
public enum ApiType {
    JSON_RPC_HTTP("json_rpc_http");

    private final String value;

    ApiType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
