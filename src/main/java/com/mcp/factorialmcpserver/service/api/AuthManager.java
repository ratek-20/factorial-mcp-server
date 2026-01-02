package com.mcp.factorialmcpserver.service.api;

import com.mcp.factorialmcpserver.model.auth.OauthToken;
import com.mcp.factorialmcpserver.service.api.request.AuthUrlFactory;
import com.mcp.factorialmcpserver.service.exception.BrowserException;
import com.mcp.factorialmcpserver.service.exception.OAuthStateMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AuthManager {

    private final AuthClient authClient;
    private final AuthUrlFactory authUrlFactory;

    private final AtomicReference<OauthToken> oauthTokenReference = new AtomicReference<>();
    private final AtomicReference<String> stateReference = new AtomicReference<>();
    private final AtomicReference<CompletableFuture<OauthToken>> waitingLoginReference = new AtomicReference<>(new CompletableFuture<>());

    private static final Logger log = LoggerFactory.getLogger(AuthManager.class);

    @Autowired
    public AuthManager(AuthClient authClient, AuthUrlFactory authUrlFactory) {
        this.authClient = authClient;
        this.authUrlFactory = authUrlFactory;
    }

    public String getValidAccessToken() {
        final OauthToken oauthToken = oauthTokenReference.get();
        if (Objects.nonNull(oauthToken) && !oauthToken.isAccessTokenExpired()) {
            return oauthToken.getAccessToken();
        }
        // TODO: refresh process
        startInteractiveLogin();
        final OauthToken newOauthToken = waitingLoginReference.get().join();
        return newOauthToken.getAccessToken();
    }

    private void startInteractiveLogin() {
        CompletableFuture<OauthToken> completableFuture = new CompletableFuture<>();
        waitingLoginReference.set(completableFuture);
        /*
        Reset the future to have 1 future per-login attempt.
        Otherwise, in case the login fails without invoking the callback (e.g., the user closes the browser),
        the thread would be indefinitely blocked.
        */

        stateReference.set(UUID.randomUUID().toString());
        /*
        To verify that the following callback is associated with the current login attempt.
        Protection against CSRF attacks.
         */

        openBrowser(authUrlFactory.create(stateReference.get()));
    }

    private void openBrowser(URI uri) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(uri);
            } catch (IOException e) {
                throw new BrowserException("Unable to open browser for Oauth2 authentication", e);
            }
        } else {
            log.info("Open the following url in a browser: {}", uri.toString());
        }

    }

    public void handleCallback(String authCode, String state) {
        final String expectedState = stateReference.get();
        if (Objects.isNull(expectedState) || !expectedState.equals(state)) {
            waitingLoginReference.get().completeExceptionally(
                    new OAuthStateMismatchException("Invalid state detected during OAuth2 authentication")
            );
            return;
        }
        try {
            final OauthToken oauthToken = authClient.getOauthToken(authCode);
            oauthTokenReference.set(oauthToken);
            waitingLoginReference.get().complete(oauthToken);
        } catch (Exception e) {
            waitingLoginReference.get().completeExceptionally(e);
        }
    }

}
