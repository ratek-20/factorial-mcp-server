package com.mcp.factorialmcpserver.service.configuration;

import com.mcp.factorialmcpserver.model.authorization.OAuthToken;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.mcp.factorialmcpserver.store.OAuthTokenStore;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthTokenLoaderTest {

    @Mock
    private OAuthTokenStore tokenStore;

    @Mock
    private AuthManager authManager;

    @Mock
    private ApplicationArguments applicationArguments;

    private OAuthTokenLoader oauthTokenLoader;

    @BeforeEach
    void setUp() {
        oauthTokenLoader = new OAuthTokenLoader(tokenStore, authManager);
    }

    @Test
    void itShouldSetOAuthTokenIfCanBeLoaded() {
        OAuthToken oauthToken = Instancio.create(OAuthToken.class);
        when(tokenStore.load()).thenReturn(Optional.of(oauthToken));

        oauthTokenLoader.run(applicationArguments);

        verify(authManager).setOAuthToken(oauthToken);
    }

    @Test
    void itShouldNotSetOAuthTokenIfCannotBeLoaded() {
        when(tokenStore.load()).thenReturn(Optional.empty());

        oauthTokenLoader.run(applicationArguments);

        verify(authManager, never()).setOAuthToken(any());
    }
}
