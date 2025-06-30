package io.github.vchhabra.a2a.jvm.core.model.enums;

import io.github.vchhabra.a2a.jvm.core.model.auth.AuthScheme;

/**
 * Enum representing the types of authentication schemes supported for securing API interactions.
 * This is used to define the method of authorization used by implementations of the {@link
 * AuthScheme} interface.
 *
 * <p>Constants: - BEARER: Refers to the Bearer authentication scheme, which uses a token-based
 * authorization mechanism. - OAUTH2: Refers to the OAuth 2.0 protocol used for delegated access and
 * token-based authorization.
 */
public enum AuthType {
    BEARER("bearer"),
    OAUTH2("oauth2");

    private final String value;

    AuthType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
