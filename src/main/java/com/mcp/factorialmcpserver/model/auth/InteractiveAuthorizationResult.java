package com.mcp.factorialmcpserver.model.auth;

public record InteractiveAuthorizationResult(boolean browserOpened, String authUrl) {
}
