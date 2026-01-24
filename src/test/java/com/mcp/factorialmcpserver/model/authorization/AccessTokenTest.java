package com.mcp.factorialmcpserver.model.authorization;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccessTokenTest {

    @Test
    void shouldBeExpiredWhenExpiresAtIsInThePast() {
        AccessToken accessToken = Instancio.of(AccessToken.class)
                .set(field(AccessToken::expiresAt), Instant.now().minusSeconds(60))
                .create();
        assertTrue(accessToken.isExpired());
    }

    @Test
    void shouldNotBeExpiredWhenExpiresAtIsInTheFuture() {
        AccessToken accessToken = Instancio.of(AccessToken.class)
                .set(field(AccessToken::expiresAt), Instant.now().plusSeconds(60))
                .create();
        assertFalse(accessToken.isExpired());
    }
}
