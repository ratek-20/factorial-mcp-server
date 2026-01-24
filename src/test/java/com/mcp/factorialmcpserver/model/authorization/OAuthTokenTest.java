package com.mcp.factorialmcpserver.model.authorization;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OAuthTokenTest {

    @Test
    void shouldReturnAccessTokenValue() {
        String tokenValue = "secret-token";
        OAuthToken oauthToken = Instancio.of(OAuthToken.class)
                .set(field(AccessToken::accessToken), tokenValue)
                .create();

        assertEquals(tokenValue, oauthToken.accessTokenValue());
    }

    @Test
    void shouldCheckIfAccessTokenIsExpired() {
        AccessToken accessToken = Mockito.mock(AccessToken.class);

        OAuthToken oauthToken = Instancio.of(OAuthToken.class)
                .set(field(OAuthToken::accessToken), accessToken)
                .create();

        Mockito.when(accessToken.isExpired()).thenReturn(true);

        assertTrue(oauthToken.isAccessTokenExpired());
    }
}
