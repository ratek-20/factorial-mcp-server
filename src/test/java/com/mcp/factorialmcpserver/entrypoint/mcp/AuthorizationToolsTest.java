package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.model.authorization.AuthResult;
import com.mcp.factorialmcpserver.model.authorization.AuthStatus;
import com.mcp.factorialmcpserver.model.authorization.InteractiveAuthorizationResult;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationToolsTest {

    @Mock
    private AuthManager authManager;

    @InjectMocks
    private AuthorizationTools authorizationTools;

    @Test
    void itShouldReturnOkWhenAlreadyAuthorized() {
        when(authManager.isAuthorized()).thenReturn(true);

        AuthResult authResult = authorizationTools.authorize();

        assertEquals(AuthStatus.OK, authResult.authStatus());
        assertNull(authResult.authUrl());
        assertEquals("Already authorized.", authResult.message());
    }

    @Test
    void itShouldReturnOpenedBrowserWhenNotAuthorizedAndBrowserOpened() {
        InteractiveAuthorizationResult interactiveResult = Instancio.of(InteractiveAuthorizationResult.class)
                .set(field(InteractiveAuthorizationResult::browserOpened), true)
                .create();
        when(authManager.isAuthorized()).thenReturn(false);
        when(authManager.tryInteractiveAuthorization()).thenReturn(interactiveResult);

        AuthResult authResult = authorizationTools.authorize();

        assertEquals(AuthStatus.OPENED_BROWSER, authResult.authStatus());
        assertEquals(interactiveResult.authUrl(), authResult.authUrl());
        assertEquals("Browser opened. Complete authorization, then retry your command.", authResult.message());
    }

    @Test
    void itShouldReturnActionRequiredWhenNotAuthorizedAndBrowserNotOpened() {
        InteractiveAuthorizationResult interactiveResult = Instancio.of(InteractiveAuthorizationResult.class)
                .set(field(InteractiveAuthorizationResult::browserOpened), false)
                .create();
        when(authManager.isAuthorized()).thenReturn(false);
        when(authManager.tryInteractiveAuthorization()).thenReturn(interactiveResult);

        AuthResult authResult = authorizationTools.authorize();

        assertEquals(AuthStatus.ACTION_REQUIRED, authResult.authStatus());
        assertEquals(interactiveResult.authUrl(), authResult.authUrl());
        assertEquals("Open authUrl in a browser, complete authorization, then retry your command.", authResult.message());
    }
}
