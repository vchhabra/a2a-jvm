package io.github.vchhabra.a2a.jvm.auth.bearer;

import io.github.vchhabra.a2a.jvm.core.model.auth.AuthScheme;
import io.github.vchhabra.a2a.jvm.core.model.enums.AuthType;

/**
 * Represents an implementation of the {@link AuthScheme} interface for the Bearer authentication
 * scheme. This class associates the Bearer authentication type with interactions requiring an
 * authentication method that leverages bearer tokens.
 */
public record Bearer() implements AuthScheme {

    @Override
    public AuthType type() {
        return AuthType.BEARER;
    }
}
