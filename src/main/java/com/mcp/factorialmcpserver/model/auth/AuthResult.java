package com.mcp.factorialmcpserver.model.auth;

public record AuthResult(
        AuthStatus authStatus,
        String authUrl,
        String message
) {}
