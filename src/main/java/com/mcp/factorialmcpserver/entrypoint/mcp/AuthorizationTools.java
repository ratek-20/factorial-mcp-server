package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.model.authorization.AuthResult;
import com.mcp.factorialmcpserver.model.authorization.AuthStatus;
import com.mcp.factorialmcpserver.model.authorization.InteractiveAuthorizationResult;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationTools {

    private final AuthManager authManager;

    @Autowired
    public AuthorizationTools(AuthManager authManager) {
        this.authManager = authManager;
    }

    @McpTool(name = "authorize", description = "Authorize access to the Factorial system using OAuth2 protocol.")
    public AuthResult authorize() {
        if (authManager.isAuthorized()) {
            return new AuthResult(
                    AuthStatus.OK,
                    null,
                    "Already authorized."
            );
        }
        final InteractiveAuthorizationResult result = authManager.tryInteractiveAuthorization();
        return result.browserOpened()
                ? new AuthResult(
                    AuthStatus.OPENED_BROWSER,
                    result.authUrl(),
            "Browser opened. Complete authorization, then retry your command."
                )
                : new AuthResult(
                    AuthStatus.ACTION_REQUIRED,
                    result.authUrl(),
            "Open authUrl in a browser, complete authorization, then retry your command."
                );
    }
}
