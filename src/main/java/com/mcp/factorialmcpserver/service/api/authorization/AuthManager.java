package com.mcp.factorialmcpserver.service.api.authorization;

import com.mcp.factorialmcpserver.model.auth.InteractiveAuthorizationResult;
import com.mcp.factorialmcpserver.model.auth.OAuthToken;
import com.mcp.factorialmcpserver.service.api.authorization.request.AuthUrlFactory;
import com.mcp.factorialmcpserver.service.exception.AuthorizationRequiredException;
import com.mcp.factorialmcpserver.service.exception.OAuthStateMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AuthManager {

    private final AuthClient authClient;
    private final AuthUrlFactory authUrlFactory;

    private final AtomicReference<OAuthToken> oauthTokenReference = new AtomicReference<>();
    private final AtomicReference<String> stateReference = new AtomicReference<>();

    private static final Logger log = LoggerFactory.getLogger(AuthManager.class);

    @Autowired
    public AuthManager(AuthClient authClient, AuthUrlFactory authUrlFactory) {
        this.authClient = authClient;
        this.authUrlFactory = authUrlFactory;
    }

    public String getValidAccessToken() {
        final OAuthToken oauthToken = oauthTokenReference.get();
        if (Objects.isNull(oauthToken)) {
            throw new AuthorizationRequiredException("Missing");
        }
        if (oauthToken.isAccessTokenExpired()) {
            try {
                final OAuthToken refurbishedToken = authClient.refreshToken(oauthToken.refreshToken());
                oauthTokenReference.set(refurbishedToken);
                return refurbishedToken.getAccessToken();
            } catch (Exception e) {
                log.error("Unable to refresh token", e);
                throw new AuthorizationRequiredException("Expired");
            }
        }
        return oauthToken.getAccessToken();
    }

    /**
     * @return true if a valid access token can be obtained right now.
     *         May trigger a refresh token request.
     */
    public boolean isAuthorized() {
        try {
            getValidAccessToken();
            return true;
        } catch (AuthorizationRequiredException e) {
            return false;
        }
    }

    public InteractiveAuthorizationResult tryInteractiveAuthorization() {
        final String state = UUID.randomUUID().toString();
        stateReference.set(state);
        /*
        The state is needed to verify that the following callback is associated with the current login attempt.
        Protection against CSRF attacks.
        It's initialized at the beginning of the authorization process and reset at the end of it. (see stateReference.getAndSet)
         */

        final URI uri = authUrlFactory.create(stateReference.get());
        if (!canOpenABrowser()) {
            return new InteractiveAuthorizationResult(false, uri.toString());
        }
        try {
            Desktop.getDesktop().browse(uri);
            return new InteractiveAuthorizationResult(true, uri.toString());
        } catch (IOException e) {
            log.error("Unable to open browser for Oauth2 authorization", e);
            return new InteractiveAuthorizationResult(false, uri.toString());
        }

    }

    private boolean canOpenABrowser() {
        return Desktop.isDesktopSupported()
                && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)
                && !GraphicsEnvironment.isHeadless();
    }

    public void handleCallback(String authCode, String state) {
        final String expectedState = stateReference.getAndSet(null);
        if (!expectedState.equals(state)) {
            throw new OAuthStateMismatchException("Invalid state detected during OAuth2 authorization.");
        }
        final OAuthToken oauthToken = authClient.requestToken(authCode);
        oauthTokenReference.set(oauthToken);
    }

}
