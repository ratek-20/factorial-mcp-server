package com.mcp.factorialmcpserver.model.authorization;

public record AuthResult(
        AuthStatus authStatus,
        String authUrl,
        String message
) {}
