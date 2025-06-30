package io.github.vchhabra.a2a.jvm.core.model.auth;

import io.github.vchhabra.a2a.jvm.core.model.enums.AuthType;

/**
 * Represents an authentication scheme used to identify and authorize interactions with an agent.
 * The authentication scheme defines the method and protocol for securing API interactions.
 */
public interface AuthScheme {

    /**
     * Get an authentication scheme type.
     *
     * @return The {@link AuthType}.
     */
    AuthType type();
}
