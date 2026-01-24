package com.mcp.factorialmcpserver.service.api.authorization;

import com.mcp.factorialmcpserver.model.authorization.AccessToken;
import com.mcp.factorialmcpserver.model.authorization.OAuthToken;
import com.mcp.factorialmcpserver.service.api.authorization.request.AuthUrlFactory;
import com.mcp.factorialmcpserver.service.exception.AuthorizationRequiredException;
import com.mcp.factorialmcpserver.service.exception.OAuthStateMismatchException;
import com.mcp.factorialmcpserver.store.OAuthTokenStore;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthManagerTest {

    private AuthManager authManager;

    @Mock
    private AuthClient authClient;

    @Mock
    private AuthUrlFactory authUrlFactory;

    @Mock
    private OAuthTokenStore tokenStore;

    @BeforeEach
    void setUp() {
        authManager = new AuthManager(authClient, authUrlFactory, tokenStore);
    }

    @Test
    void itShouldThrowWhenTokenIsMissing() {
        assertThrows(AuthorizationRequiredException.class, () -> authManager.getValidAccessToken());
    }

    @Test
    void itShouldReturnAccessTokenWhenItIsStillValid() {
        OAuthToken oauthToken = Instancio.of(OAuthToken.class)
                .set(field(AccessToken::expiresAt), Instant.now().plusSeconds(3600))
                .create();
        authManager.setOAuthToken(oauthToken);

        String tokenValue = authManager.getValidAccessToken();

        assertEquals(oauthToken.accessTokenValue(), tokenValue);
        verifyNoInteractions(authClient);
    }

    @Test
    void itShouldRefreshTokenWhenAccessTokenIsExpired() {
        OAuthToken expiredToken = Instancio.of(OAuthToken.class)
                .set(field(AccessToken::expiresAt), Instant.now().minusSeconds(1))
                .create();
        authManager.setOAuthToken(expiredToken);

        OAuthToken refurbishedToken = Instancio.create(OAuthToken.class);
        
        when(authClient.refreshToken(expiredToken.refreshToken())).thenReturn(refurbishedToken);

        String tokenValue = authManager.getValidAccessToken();

        assertEquals(refurbishedToken.accessTokenValue(), tokenValue);
        verify(authClient).refreshToken(expiredToken.refreshToken());
        verify(tokenStore).save(refurbishedToken);
    }

    @Test
    void itShouldThrowWhenTokenRefreshFails() {
        OAuthToken expiredToken = Instancio.of(OAuthToken.class)
                .set(field(AccessToken::expiresAt), Instant.now().minusSeconds(1))
                .create();
        authManager.setOAuthToken(expiredToken);

        when(authClient.refreshToken(anyString())).thenThrow(new RuntimeException("Refresh failed"));

        assertThrows(AuthorizationRequiredException.class, () -> authManager.getValidAccessToken());
    }

    @Test
    void itShouldReturnTrueWhenAuthorized() {
        OAuthToken oauthToken = Instancio.of(OAuthToken.class)
                .set(field(AccessToken::expiresAt), Instant.now().plusSeconds(3600))
                .create();
        authManager.setOAuthToken(oauthToken);

        assertTrue(authManager.isAuthorized());
    }

    @Test
    void itShouldReturnFalseWhenNotAuthorized() {
        assertFalse(authManager.isAuthorized());
    }

    @Test
    void itShouldRequestAndSaveTokenOnValidCallback() throws Exception {
        String expectedState = "expected-state";
        setState(expectedState);

        String authCode = "auth-code";
        OAuthToken oauthToken = Instancio.create(OAuthToken.class);
        when(authClient.requestToken(authCode)).thenReturn(oauthToken);

        authManager.handleCallback(authCode, expectedState);

        verify(authClient).requestToken(authCode);
        verify(tokenStore).save(oauthToken);
    }

    @Test
    void itShouldThrowWhenCallbackStateIsInvalid() throws Exception {
        setState("expected-state");

        assertThrows(OAuthStateMismatchException.class, () -> authManager.handleCallback("auth-code", "wrong-state"));
    }

    private void setState(String state) throws Exception {
        var field = AuthManager.class.getDeclaredField("stateReference");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        AtomicReference<String> stateReference = (AtomicReference<String>) field.get(authManager);
        stateReference.set(state);
    }

    /*
     * The interactive authorization process is not covered by unit tests.
     * This is because it triggers an external browser opening and its success depends on the
     * underlying system environment (e.g., availability of a Desktop environment, browser configuration),
     * which is unpredictable and side-effect-heavy for a unit testing context.
     */
}
