package io.github.vchhabra.a2a.jvm.core.model;

import java.net.URI;

import io.github.vchhabra.a2a.jvm.core.model.enums.ApiType;

/**
 * Describes the API endpoint for task management.
 *
 * @param type The type of API, e.g., "json_rpc_http".
 * @param url The URL endpoint for creating and managing tasks.
 */
public record Api(ApiType type, URI url) {}
